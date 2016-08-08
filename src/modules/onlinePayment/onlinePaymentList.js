(function(app) {
  'use strict';

  var onlinePaymentListCtrl = function($scope) {
    //初始化支付状态查询下拉菜单
    $scope.searchStr = {
      searchStatus: ''
    };
    $scope.searchStatusTypes = [
      {code:'', name :'全部'},
      {code:'0', name :'未支付'},
      {code:'1', name :'已支付'}
    ];

    //查询缴费列表事件
    $scope.searchPay = function() {

    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('onlinePaymentList', {
      url: '/onlinePayment/onlinePaymentList',
      cache: 'false',
      templateUrl: 'modules/onlinePayment/onlinePaymentList.html',
      controller: onlinePaymentListCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
