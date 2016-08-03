(function(app) {
  'use strict';

  var onlinePaymentCtrl = function($scope) {
    $scope.searchStr = {
      searchStatus: ''
    };
    $scope.searchStatusTypes = [
      {code:'', name :'全部'},
      {code:'0', name :'未支付'},
      {code:'1', name :'已支付'}
    ];
    $scope.searchChange = function() {

    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('mainOnlinePayment', {
      url: '/main/onlinePayment',
      templateUrl: 'modules/main/onlinePayment/onlinePayment.html',
      controller: onlinePaymentCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
