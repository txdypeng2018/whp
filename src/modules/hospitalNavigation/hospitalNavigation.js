(function(app) {
  'use strict';

  var hospitalNavigationCtrl = function($scope, $http, $timeout , $state, toastService) {
    //取得院区及建筑
    var getBuilds = function() {
      $http.get('/hospitalNavigation/builds').success(function(data) {
        $scope.districtBuilds = data;
      }).error(function(data){
        toastService.show(data);
      });
    };

    $scope.$on('$ionicView.beforeEnter', function(){
      if (angular.isUndefined($scope.districtBuilds) || $scope.districtBuilds.length === 0) {
        getBuilds();
      }
    });

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
