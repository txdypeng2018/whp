(function(app) {
  'use strict';

  var paymentSelectCtrl = function($scope, $http, $state, $stateParams, appConstants, toastService, $ionicPopup, $ionicHistory) {
    var orderNum = $stateParams.orderNum;
    $scope.needTime = true;

    var getFromPage = function() {
      for (var i in $ionicHistory.viewHistory().views) {
        var view = $ionicHistory.viewHistory().views[i];
        if (view.stateName === 'onlinePaymentList') {
          $scope.needTime = false;
        }
      }
    };
    getFromPage();

    //倒计时
    var updateTime = function() {
      if (!angular.isUndefined($scope.time.second)) {
        //倒计时结束终止支付
        if ($scope.time.minute <= 0 && $scope.time.second <= 0) {
          if ($scope.needTime) {
            $state.go('tab.main');
            toastService.show('交易超时');
          }
        }
        if ($scope.time.second === 0) {
          --$scope.time.minute;
          $scope.time.second = 59;
        }
        else {
          --$scope.time.second;
        }
      }
    };

    //取得挂号单信息
    $http.get('/orders/'+orderNum).success(function(data) {
      $scope.order = data;
      if ($scope.order.isAppointment === '0') {
        $scope.needTime = false;
      }
      $scope.time = {
        minute: $scope.order.closeTime[0],
        second: $scope.order.closeTime[1]
      };
      setInterval(function(){
        $scope.$apply(updateTime);
      }, 1000);

      // 总金额 必填
      $scope.amount = data.amount;
      // 商品名称
      $scope.subject = data.name;
      // 商品描述
      $scope.body  = data.description;
    });

    //支付方式选择事件
    $scope.paySelectValue = '';
    $scope.paymentSelect = function(value) {
      $scope.paySelectValue = value;
      angular.element(document.querySelectorAll('.select-yes')).addClass('select-none');
      angular.element(document.querySelectorAll('.select-no')).removeClass('select-none');
      angular.element(document.getElementById('select_yes_'+value)).removeClass('select-none');
      angular.element(document.getElementById('select_no_'+value)).addClass('select-none');
    };

    $scope.pay = function() {
      if(angular.isUndefined($scope.amount)) {
        toastService.show('获取订单信息失败,请稍后重试!');
      } else {
        // 订单总金额，将"元"转换为"分"
        var payamount = Number($scope.amount) * 100;
        //一网通支付
        if ($scope.paySelectValue === '1') {
          var cmbprepay = {
            billNo: orderNum,
            merchantPara: $scope.body,
            amount: payamount
          };
          $http.post('/pay/cmb/prepay', cmbprepay).success(function (data) {
            console.debug('data:', data);
            var cmbquery = {
              billNo: data.cmbBillNo,
              date: data.cmbDate
            };
            // 如果请求结果正常
            if (data.resultCode === 'SUCCESS') {
              // 调用一网通支付
              cmbpay.payment(
                data,
                function (retData) {
                  console.debug('retData:' + retData);
                  var converseRet = angular.fromJson(retData);
                  console.debug('converseRet:' + converseRet);
                  if (converseRet.resultCode === '0') {
                    $http.post('/pay/cmb/queryCmbPay', cmbquery).success(function (queryData) {
                      if (queryData.resultCode === 'SUCCESS') {
                        $state.go('paymentResult', {
                          resultImgSrc: './assets/images/choosen.png',
                          resultText: '支付成功!',
                          memberId: $stateParams.memberId
                        });
                      } else {
                        var failPopup = $ionicPopup.show({
                          template: '<div style="padding: 3px;font-size:15px; text-align:center;">' + '未能支付成功' + '</div>',
                          title: '温馨提示',
                          buttons: [
                            {
                              text: '我知道了',
                              type: 'positive',
                              onTap: function (e) {
                                e.preventDefault();
                                failPopup.close();
                              }
                            }
                          ]
                        });
                      }
                    }).error(function (data) {
                      console.debug('data', data);
                      toastService.show(data);
                    });
                  } else if (data.resultCode === 'SYSERROR') {
                    console.debug('data', data);
                    toastService.show(data.resultMsg);
                  }
                }, function (retData) {
                  console.debug('retData', retData);
                  toastService.show(retData);
                });
            } else if(data.resultCode === 'SYSERROR'){
              console.debug('data', data);
              toastService.show(data.resultMsg);
            }
          }).error(function (data) {
              console.debug('data', data);
              toastService.show(data);
            }
          );
        } else
        //支付宝支付
        if ($scope.paySelectValue === '2') {
          var aliprepay = {
            outTradeNo: orderNum,
            body: $scope.body,
            totalFee: payamount
          };
          $http.post('/pay/ali/prepay', aliprepay).success(function (data) {
            console.debug('data:', data);
            // 如果请求结果正常
            if (data.resultCode === 'SUCCESS') {
              // 调用支付宝支付
              alipay.payment(
                data,
                function (retData) {
                  var converseRet = angular.fromJson(retData);
                  var failImagesSrc = './assets/images/umeng_update_close_bg_tap.png';
                  // 订单支付成功
                  if(converseRet.resultStatus === '9000') {
                    $state.go('paymentResult', {resultImgSrc: './assets/images/choosen.png', resultText: '支付成功!', memberId: $stateParams.memberId});
                    // 正在处理中，支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
                    // 支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
                  } else if(converseRet.resultStatus === '8000' || converseRet.resultStatus === '6004') {
                    $state.go('paymentResult', {resultImgSrc: failImagesSrc, resultText: '支付结果未知!请查看挂号页!', memberId: $stateParams.memberId});
                    // 用户中途取消
                  } else if(converseRet.resultStatus === '6001') {
                    var myPopup = $ionicPopup.show({
                      template: '<div style="padding: 3px;font-size:15px; text-align:center;">'+'未能支付成功'+'</div>',
                      title: '温馨提示',
                      buttons: [
                        {
                          text: '我知道了',
                          type: 'positive',
                          onTap: function(e) {
                            e.preventDefault();
                            myPopup.close();
                          }
                        }
                      ]
                    });
                    // 订单支付失败
                  } else if(converseRet.resultStatus === '4000') {
                    $state.go('paymentResult', {resultImgSrc: failImagesSrc, resultText: '支付失败!', memberId: $stateParams.memberId});
                    // 网络连接出错
                  } else if(converseRet.resultStatus === '6002') {
                    $state.go('paymentResult', {resultImgSrc: failImagesSrc, resultText: '支付失败!网络连接出错!', memberId: $stateParams.memberId});
                    // 其它支付错误
                  } else {
                    $state.go('paymentResult', {resultImgSrc: failImagesSrc, resultText: '支付失败!未知错误!', memberId: $stateParams.memberId});
                  }
                }, function (retData) {
                  console.debug('retData', retData);
                  toastService.show(retData);
                });
            } else if(data.resultCode === 'SYSERROR'){
              console.debug('data', data);
              toastService.show(data.resultMsg);
            }
          }).error(function (data) {
            console.debug('data', data);
            toastService.show(data);
          });

          //微信支付
        } else if ($scope.paySelectValue === '3') {
          var weixinpay = {
            outTradeNo: orderNum,
            body: $scope.body,
            totalFee: payamount
          };
          $http.post('/pay/wechat/prepay', weixinpay).success(function (data) {
            console.debug('data:', data);
            // 如果请求结果正常
            if (data.resultCode === 'SUCCESS') {
              var reqdata = {
                appid: data.appid,
                noncestr: data.noncestr,
                package: data.package,
                partnerid: data.partnerid,
                prepayid: data.prepayid,
                timestamp: data.timestamp,
                sign: data.sign
              };

              // 调用微信支付
              wxpay.payment(
                reqdata,
                // 支付成功
                function (retData) {
                  console.debug('retData', retData);
                  var converseRet = angular.fromJson(retData);
                  var failImagesSrc = './assets/images/umeng_update_close_bg_tap.png';
                  // 正常支付
                  if(converseRet.code === 0) {
                    $state.go('paymentResult', {resultImgSrc: './assets/images/choosen.png', resultText: '支付成功!', memberId: $stateParams.memberId});
                    // 认证被否决
                  } else if(converseRet.code === -4) {
                    $state.go('paymentResult', {resultImgSrc: failImagesSrc, resultText: '支付失败!认证被否决!', memberId: $stateParams.memberId});
                    // 一般错误
                  } else if(converseRet.code === -1) {
                    $state.go('paymentResult', {resultImgSrc: failImagesSrc, resultText: '支付失败!一般错误!', memberId: $stateParams.memberId});
                    // 发送失败
                  } else if(converseRet.code === -3) {
                    $state.go('paymentResult', {resultImgSrc: failImagesSrc, resultText: '支付失败!发送失败!', memberId: $stateParams.memberId});
                    // 不支持错误
                  } else if(converseRet.code === -5) {
                    $state.go('paymentResult', {resultImgSrc: failImagesSrc, resultText: '支付失败!不支持错误!', memberId: $stateParams.memberId});
                    // 用户取消
                  } else if(converseRet.code === -2) {
                    var myPopup = $ionicPopup.show({
                      template: '<div style="padding: 3px;font-size:15px; text-align:center;">'+'未能支付成功'+'</div>',
                      title: '温馨提示',
                      buttons: [
                        {
                          text: '我知道了',
                          type: 'positive',
                          onTap: function(e) {
                            e.preventDefault();
                            myPopup.close();
                          }
                        }
                      ]
                    });
                  }
                  // 支付异常
                }, function (retData) {
                  console.debug('retData:', retData);
                  toastService.show(retData);
                }).error(function (data) {
                  console.debug('data', data);
                  toastService.show(data);
                });
            } else if(data.resultCode === 'SYSERROR'){
              console.debug('data', data);
              toastService.show(data.resultMsg);
            }
          }).error(function (data) {
            console.debug('data', data);
            toastService.show(data);
          });
        }
      }
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('paymentSelect', {
      url: '/onlinePayment/paymentSelect/:orderNum/:memberId',
      cache: 'false',
      templateUrl: 'modules/onlinePayment/paymentSelect.html',
      controller: paymentSelectCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
