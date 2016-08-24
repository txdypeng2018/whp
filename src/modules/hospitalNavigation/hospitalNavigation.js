(function(app) {
  'use strict';

  var hospitalNavigationCtrl = function($scope, $http, $timeout , $state, $ionicHistory) {
    //取得院区及建筑
    $http.get('/hospitalNavigation/builds').success(function(data) {
      $scope.districtBuilds = data;
    });

    //返回上页
    $scope.goBack = function() {
      $ionicHistory.goBack();
    };

    //楼选中事件
    $scope.buildingClk = function(id) {
      $state.go('hospitalNavigationFloorList', {id: id});
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('hospitalNavigation', {
      url: '/hospitalNavigation',
      templateUrl: 'modules/hospitalNavigation/hospitalNavigation.html',
      controller: hospitalNavigationCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
