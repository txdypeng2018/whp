(function(app) {
  'use strict';

  var settingIndexCtrl = function($scope, $state) {
    //路由跳转
    $scope.itemRouter = function(routerId) {
      $state.go(routerId);
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('settingIndex', {
      url: '/setting/settingIndex',
      templateUrl: 'modules/setting/settingIndex.html',
      controller: settingIndexCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
