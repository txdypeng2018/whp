(function(app) {
  'use strict';

  var registrationDoctorListCtrl = function() {

  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('mainRegistrationDoctorList', {
      url: '/main/registration/registrationDoctorList/:id',
      templateUrl: 'modules/main/registration/registrationDoctorList.html',
      controller: registrationDoctorListCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
