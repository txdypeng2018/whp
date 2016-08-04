(function(app) {
  'use strict';

  var registrationDoctorListCtrl = function($scope, $http, $state, $stateParams) {
    var id = $stateParams.id;
    $http.get('/main/registration/doctorList', {params: {id: id}}).success(function(data) {
      $scope.doctors = data;
    });

    $scope.doctorClk = function(id) {
      $state.go('mainRegistrationDoctor', {id: id});
    };
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
