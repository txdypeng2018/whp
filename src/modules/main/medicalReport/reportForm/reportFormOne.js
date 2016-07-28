(function(app) {
  'use strict';

  var reportFormOneCtrl = function($scope, $http, $stateParams) {
    $http.get('/main/medicalReport/data', {params: {id: $stateParams.id}}).success(function(data) {
      $scope.report = data;
    });
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('mainMedicalReportFormOne', {
      url: '/main/medicalReport/reportFormOne/:id',
      cache: 'false',
      templateUrl: 'modules/main/medicalReport/reportForm/reportFormOne.html',
      controller: reportFormOneCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
