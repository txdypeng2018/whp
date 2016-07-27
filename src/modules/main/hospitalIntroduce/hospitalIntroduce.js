(function(app) {
  'use strict';

  var hospitalIntroduceCtrl = function($scope, $http) {
    $http.get('/main/hospitalIntroduce').success(function(data) {
      $scope.hospitalIntroduce = data;
    });
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('mainHospitalIntroduce', {
      url: '/main/hospitalIntroduce',
      templateUrl: 'modules/main/hospitalIntroduce/hospitalIntroduce.html',
      controller: hospitalIntroduceCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
