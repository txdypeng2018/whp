(function(app) {
  'use strict';

  var medicalGuideCtrl = function($scope, $http) {
    $http.get('/main/medicalGuide').success(function(data) {
      $scope.medicalGuide = data;
    });
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
