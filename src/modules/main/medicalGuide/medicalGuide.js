(function(app) {
  'use strict';

  var medicalGuideCtrl = function($scope) {

  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('mainMedicalGuide', {
      url: '/main/medicalGuide',
      templateUrl: 'modules/main/medicalGuide/medicalGuide.html',
      controller: medicalGuideCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
