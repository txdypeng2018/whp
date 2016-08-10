(function(app) {
  'use strict';

  var registrationViewCtrl = function($scope, $state) {
    $scope.illustrationClk = function() {
      $state.go('registrationIllustration');
    }
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('registrationView', {
      url: '/registration/registrationView',
      templateUrl: 'modules/tab/registration/registrationView.html',
      controller: registrationViewCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
