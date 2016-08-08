(function(app) {
  'use strict';

  var paymentSelectCtrl = function($scope, $http, $state, $stateParams) {
    var category = $stateParams.category;
    var id = $stateParams.id;

    if (category === '1') {
      //取得挂号单信息
      $http.get('/register/registration', {params: {id: id}}).success(function(data) {
        $scope.amount = data.amount;
      });
    }

    //支付方式选择事件
    $scope.paySelectValue = '';
    $scope.paymentSelect = function(value) {
      $scope.paySelectValue = value;
      angular.element(document.querySelectorAll('.select-yes')).addClass('select-none');
      angular.element(document.querySelectorAll('.select-no')).removeClass('select-none');
      angular.element(document.getElementById('select_yes_'+value)).removeClass('select-none');
      angular.element(document.getElementById('select_no_'+value)).addClass('select-none');
    };

    //倒计时
    angular.element(document.getElementById('payment_countDown')).html('30分钟');
    var time = {
      minute: 30,
      second: 0
    };
    setInterval(function(){
      //倒计时结束终止支付
      if (time.minute === 0 && time.second === 0) {
        $state.go('tab.main');
      }

      if (time.second === 0) {
        --time.minute;
        time.second = 59;
      }
      else {
        --time.second;
      }
      angular.element(document.getElementById('payment_countDown')).html(time.minute + '分钟' + time.second + '秒');
    }, 1000);
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('paymentSelect', {
      url: '/onlinePayment/paymentSelect/:category/:id',
      templateUrl: 'modules/onlinePayment/paymentSelect.html',
      controller: paymentSelectCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
