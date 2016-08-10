(function(app) {
  'use strict';

  var registrationIllustrationCtrl = function($scope, $state) {
    $scope.medicalGuideClk = function() {
      $state.go('medicalGuide');
    }
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('registrationIllustration', {
      url: '/registration/registrationIllustration',
      templateUrl: 'modules/tab/registration/registrationIllustration.html',
      controller: registrationIllustrationCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
