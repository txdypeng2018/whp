(function(app) {
  'use strict';

  var registrationViewCtrl = function($scope, $http, $state, $stateParams) {
    //取得挂号单
    $http.get('/register/registrations/registration', {params: {id: $stateParams.registrationId}}).success(function(data) {
      $scope.registration = data;
    });

    //就诊、退号说明
    $scope.illustrationClk = function() {
      $state.go('registrationIllustration');
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('registrationView', {
      url: '/registration/registrationView/:registrationId',
      templateUrl: 'modules/tab/registration/registrationView.html',
      controller: registrationViewCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
