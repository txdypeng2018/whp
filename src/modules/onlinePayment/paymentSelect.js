(function(app) {
  'use strict';

  var paymentSelectCtrl = function($scope, $http, $state, $stateParams, appConstants, $cordovaToast, $ionicPopup) {
    var orderNum = $stateParams.orderNum;
    //倒计时
    var updateTime = function() {
      if (!angular.isUndefined($scope.time.second)) {
        //倒计时结束终止支付
        if ($scope.time.minute === 0 && $scope.time.second === 0) {
          $state.go('tab.main');
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
      $scope.time = {
        minute: $scope.order.closeTime[0],
        second: $scope.order.closeTime[1]
      };
      setInterval(function(){
        $scope.$apply(updateTime);
      }, 1000);

      // 总金额 必填 订单总金额，单位为"元"
      $scope.amount = data.amount;
      // 商品名称
      $scope.subject = data.name;
      // 商品描述
      $scope.body  = data.description;
    });

    // TODO 因为接口还没有实现,使用暂时的数据
    // 暂时生成临时订单号
    orderNum = new Date().getTime();
    // 总金额 必填 订单总金额，单位为"元"
    $scope.amount = '0.01';
    // 商品名称
    $scope.subject = '挂号费';
    // 商品描述
    $scope.body = '门诊挂号费用';

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
        $cordovaToast.showShortBottom('获取订单信息失败,请稍后重试!');
      } else {
        //支付宝支付
        if ($scope.paySelectValue === '2') {
          var aliprepay = {
            outTradeNo: orderNum,
            subject: $scope.subject,
            body: $scope.body,
            totalFee: $scope.amount
          };
          $http.post(appConstants.paymentServer + '/pay/ali/prepayInfo', aliprepay).success(function (data) {
            console.debug('data:', data);
            // 如果请求结果正常
            if (data.resultCode === '0') {
              // 调用支付宝支付
              alipay.payment(
                data,
                function (retData) {
                  var converseRet = angular.fromJson(retData);
                  var failImagesSrc = './assets/images/umeng_update_close_bg_tap.png';
                  // 订单支付成功
                  if(converseRet.resultStatus === '9000') {
                    $state.go('paymentResult', {resultImgSrc: './assets/images/choosen.png', resultText: '支付成功!'});
                    // 正在处理中，支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
                    // 支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
                  } else if(converseRet.resultStatus === '8000' || converseRet.resultStatus === '6004') {
                    $state.go('paymentResult', {resultImgSrc: failImagesSrc, resultText: '支付结果未知!请查看诊疗页!'});
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
                    $state.go('paymentResult', {resultImgSrc: failImagesSrc, resultText: '支付失败!'});
                    // 网络连接出错
                  } else if(converseRet.resultStatus === '6002') {
                    $state.go('paymentResult', {resultImgSrc: failImagesSrc, resultText: '支付失败!网络连接出错!'});
                    // 其它支付错误
                  } else {
                    $state.go('paymentResult', {resultImgSrc: failImagesSrc, resultText: '支付失败!未知错误!'});
                  }
                }, function (retData) {
                  console.debug('retData', retData);
                  $cordovaToast.showShortBottom('内部错误!请联系管理员!');
              });
            }
          }).error(function (data) {
            console.debug('data', data);
            $cordovaToast.showShortBottom('请求服务端数据错误!请联系管理员!');
          });

          //微信支付
        } else if ($scope.paySelectValue === '3') {
          // 总金额 必填 订单总金额，单位为分
          var totalFee = Number($scope.amount.replace('\.', ''));
          var weixinpay = {
            outTradeNo: orderNum,
            body: $scope.body,
            detail: $scope.subject,
            totalFee: totalFee
          };
          $http.post(appConstants.paymentServer + '/pay/weixin/prepayInfo', weixinpay).success(function (data) {
            console.debug('data:', data);
            // 如果请求结果正常
            if (data.resultCode === '0') {
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
                  $state.go('paymentResult', {resultImgSrc: './assets/images/choosen.png', resultText: '支付成功!'});
                  // 支付异常
                }, function (retData) {
                  console.debug('retData', retData);
                  var converseRet = angular.fromJson(retData);
                  $cordovaToast.showShortBottom(retData);
                  var failImagesSrc = './assets/images/umeng_update_close_bg_tap.png';
                  // 认证被否决
                  if(converseRet.code === -4) {
                    $state.go('paymentResult', {resultImgSrc: failImagesSrc, resultText: '支付失败!认证被否决!'});
                    // 一般错误
                  } else if(converseRet.code === -1) {
                    $state.go('paymentResult', {resultImgSrc: failImagesSrc, resultText: '支付失败!一般错误!'});
                    // 发送失败
                  } else if(converseRet.code === -3) {
                    $state.go('paymentResult', {resultImgSrc: failImagesSrc, resultText: '支付失败!发送失败!'});
                    // 不支持错误
                  } else if(converseRet.code === -5) {
                    $state.go('paymentResult', {resultImgSrc: failImagesSrc, resultText: '支付失败!不支持错误!'});
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
                  } else {
                    $cordovaToast.showShortBottom('内部错误!请联系管理员!');
                  }
                }).error(function (data) {
                  console.debug('data', data);
                  $cordovaToast.showShortBottom('请求服务端数据错误!请联系管理员!');
                });
            }
          });
        }
      }
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('paymentSelect', {
      url: '/onlinePayment/paymentSelect/:orderNum',
      cache: 'false',
      templateUrl: 'modules/onlinePayment/paymentSelect.html',
      controller: paymentSelectCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
