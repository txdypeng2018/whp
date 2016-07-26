(function(app) {
  'use strict';

  var registerCtrl = function($scope) {

  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('tab.register', {
      url: '/register',
      views:{
        'tab-register':{
          templateUrl: "modules/register/register.html",
          controller: registerCtrl
        }
      }
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
