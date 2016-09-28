(function(app) {
  'use strict';

  var paymentResultCtrl = function($scope, $http, $state, $stateParams) {

    $scope.resultImgSrc = $stateParams.resultImgSrc;
    $scope.resultText = $stateParams.resultText;

    //返回主页
    $scope.goBack = function() {
      $state.go('tab.main');
    };

    //跳转到挂号页
    $scope.viewRegistration = function() {
      $state.go('tab.registration');
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('paymentResult', {
      url: '/onlinePayment/paymentResult/:resultImgSrc/:resultText',
      cache: 'false',
      templateUrl: 'modules/onlinePayment/paymentResult.html',
      controller: paymentResultCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
