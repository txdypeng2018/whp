(function(app) {
  'use strict';

  var introductionViewCtrl = function($scope, $http, $stateParams) {
    $http.get('/main/doctorIntroduction/data', {params: {id: $stateParams.id}}).success(function(data) {
      $scope.introduction = data;
    });
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('mainDoctorIntroductionView', {
      url: '/main/doctorIntroduction/introductionView/:id',
      cache: 'false',
      templateUrl: 'modules/main/doctorIntroduction/introductionView/introductionView.html',
      controller: introductionViewCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
