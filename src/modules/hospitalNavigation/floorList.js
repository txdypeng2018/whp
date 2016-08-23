(function(app) {
  'use strict';

  var hospitalNavigationDloorListCtrl = function($scope, $http, $stateParams, $ionicHistory) {
    //取得各楼层信息
    $http.get('/hospitalNavigation/builds/floors', {params: {buildId: $stateParams.id}}).success(function(data) {
      $scope.floorList = data;
    });

    //返回上页
    $scope.goBack = function() {
      $ionicHistory.goBack();
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('hospitalNavigationFloorList', {
      url: '/hospitalNavigation/floorList/:id',
      templateUrl: 'modules/hospitalNavigation/floorList.html',
      controller: hospitalNavigationDloorListCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
