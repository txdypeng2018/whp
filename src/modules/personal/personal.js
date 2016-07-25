(function(app) {
  'use strict';

  var personalCtrl = function($scope) {

  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('tab.personal', {
      url: '/personal',
      views:{
        'tab-personal':{
          templateUrl: "modules/personal/personal.html",
          controller: personalCtrl
        }
      }
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
