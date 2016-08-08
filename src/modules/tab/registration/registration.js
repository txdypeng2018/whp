(function(app) {
  'use strict';

  var tabRegistrationCtrl = function() {

  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('tab.registration', {
      url: '/registration',
      views:{
        'tab-registration':{
          templateUrl: 'modules/tab/registration/registration.html',
          controller: tabRegistrationCtrl
        }
      }
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
