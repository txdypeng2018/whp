(function(app) {
  'use strict';

  var paymentSelectCtrl = function($scope, $http, $state, $stateParams, appConstants, $cordovaToast) {
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
    }).error(function(data){
      $cordovaToast.showShortBottom(data);
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
      //支付宝支付
      if($scope.paySelectValue === '2') {
        // 商品名称
        var subject = '挂号费';
        // 商品描述
        var alibody = '门诊挂号费用';
        // 总金额 必填 订单总金额，单位为分
        var alitotalFee = '0.01';

        $http({
          method: 'post',
          url: appConstants.paymentServer + '/api/getALIpayInfo',
          params: {
            'subject': subject,
            'body': alibody,
            'total_fee': alitotalFee
          },
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
          }
        }).success(function (data) {
          console.debug('data:', data);

          // 如果请求结果正常
          if (data.resultCode === '0') {
            // 调用支付宝支付
            alipay.payment(
              data,
              function (retData) {
                console.debug('retData', retData);
                //alert(retData); //TODO
              }, function (retData) {
                console.debug('retData', retData);
                //alert(retData); //TODO
              });
          }
        });

        //微信支付
      } else if ($scope.paySelectValue === '3') {
        // 设备号 终端设备号(门店号或收银设备ID)，默认请传"WEB"
        var deviceInfo = 'WEB';
        // 商品描述 必填  —需传入应用市场上的APP名字-实际商品名称，天天爱消除-游戏充值。
        var body = '盛京医院支付测试';
        // 商品详情
        var detail = '盛京医院支付详情';
        // 附加数据
        var attach = '圣经医院支付附加数据';
        // 总金额 必填 订单总金额，单位为分
        var totalFee = '1';

        $http({
          method: 'post',
          url: appConstants.paymentServer + '/api/getWXPrepayInfo',
          params: {
            'body': body,
            'device_info': deviceInfo,
            'detail': detail,
            'attach': attach,
            'total_fee': totalFee
          },
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
          }
        }).success(function (data) {
          console.debug('resultCode:', data.resultCode);
          console.debug('resultMessage:', data.resultMessage);
          console.debug('appid:', data.appid);
          console.debug('partnerid:', data.partnerid);
          console.debug('prepayid:', data.prepayid);
          console.debug('package:', data.package);
          console.debug('noncestr:', data.noncestr);
          console.debug('timestamp:', data.timestamp);
          console.debug('sign:', data.sign);

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
              function (retData) {
                console.debug('retData', retData);
                //alert(retData); //TODO
              }, function (retData) {
                console.debug('retData', retData);
                //alert(retData); //TODO
              });
          }
        });
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
