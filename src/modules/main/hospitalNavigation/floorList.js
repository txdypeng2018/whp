(function(app) {
  'use strict';

  var floorListCtrl = function() {

  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('mainFloorList', {
      url: '/main/floorList',
      templateUrl: 'modules/main/hospitalNavigation/floorList.html',
      controller: floorListCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
