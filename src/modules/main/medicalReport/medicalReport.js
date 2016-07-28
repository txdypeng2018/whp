(function(app) {
  'use strict';

  var medicalReportCtrl = function() {

  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('mainMedicalReport', {
      url: '/main/medicalReport',
      templateUrl: 'modules/main/medicalReport/medicalReport.html',
      controller: medicalReportCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
