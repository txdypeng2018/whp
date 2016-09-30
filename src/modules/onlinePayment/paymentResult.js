(function(app) {
  'use strict';

  var paymentResultCtrl = function($scope, $http, $state, $stateParams, $ionicHistory) {
    $scope.resultImgSrc = $stateParams.resultImgSrc;
    $scope.resultText = $stateParams.resultText;

    var getFromPage = function() {
      $scope.fromPage = 'register';
      for (var i in $ionicHistory.viewHistory().views) {
        var view = $ionicHistory.viewHistory().views[i];
        if (view.stateName === 'onlinePaymentList') {
          $scope.fromPage = 'pay';
        }
      }
    };
    getFromPage();

    //返回主页
    $scope.goBack = function() {
      $state.go('tab.main');
    };

    //跳转到挂号页
    $scope.viewRegistration = function() {
      if ($scope.fromPage === 'pay') {
        $state.go('onlinePaymentList');
      }
      else {
        $state.go('tab.registration');
      }
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
