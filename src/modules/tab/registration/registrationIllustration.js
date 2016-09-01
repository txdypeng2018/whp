(function(app) {
  'use strict';

  var registrationIllustrationCtrl = function($scope, $state, $http, $cordovaToast) {
    //取得退号就诊说明
    $http.get('/register/visitAndBacknumDesc').success(function(data) {
      $scope.description = data;
    }).error(function(data){
      $cordovaToast.showShortBottom(data);
    });

    $scope.medicalGuideClk = function() {
      $state.go('medicalGuide');
    };
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
