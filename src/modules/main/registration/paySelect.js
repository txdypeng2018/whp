(function(app) {
  'use strict';

  var paySelectCtrl = function() {

  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('mainPaySelect', {
      url: '/main/paySelect',
      templateUrl: 'modules/main/registration/paySelect.html',
      controller: paySelectCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
