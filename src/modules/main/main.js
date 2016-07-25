(function(app) {
  'use strict';

  var mainCtrl = function($scope) {

  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('tab.main', {
      url: '/main',
      views:{
        'tab-main':{
          templateUrl: "modules/main/main.html",
          controller: mainCtrl
        }
      }
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
