(function(app) {
  'use strict';

  var doctorIntroductionCtrl = function($scope) {
    $scope.doSearch = function() {

    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('mainDoctorIntroduction', {
      url: '/main/doctorIntroduction',
      templateUrl: 'modules/main/doctorIntroduction/doctorIntroduction.html',
      controller: doctorIntroductionCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
