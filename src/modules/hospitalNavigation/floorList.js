(function(app) {
  'use strict';

  var hospitalNavigationFloorListCtrl = function($scope, $http, $stateParams, toastService) {
    //取得各楼层信息
    var getFloors = function() {
      $http.get('/hospitalNavigation/builds/floors', {params: {buildId: $stateParams.id}}).success(function(data) {
        $scope.floorList = data;
      }).error(function(data){
        toastService.show(data);
      });
    };

    $scope.$on('$ionicView.beforeEnter', function(){
      if (angular.isUndefined($scope.floorList) || $scope.floorList.length === 0) {
        getFloors();
      }
    });
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('hospitalNavigationFloorList', {
      url: '/hospitalNavigation/floorList/:id',
      templateUrl: 'modules/hospitalNavigation/floorList.html',
      controller: hospitalNavigationFloorListCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
