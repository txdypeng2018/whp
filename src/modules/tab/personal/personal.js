(function(app) {
  'use strict';

  var tabPersonalCtrl = function() {

  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('tab.personal', {
      url: '/personal',
      views:{
        'tab-personal':{
          templateUrl: 'modules/tab/personal/personal.html',
          controller: tabPersonalCtrl
        }
      }
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
