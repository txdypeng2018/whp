(function(app) {
  'use strict';

  var medicalGuideCtrl = function($scope, $http) {
    //取得就医指南信息
    $http.get('/medicalGuide').success(function(data) {
      $scope.medicalGuide = data;
    });
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('medicalGuide', {
      url: '/medicalGuide',
      templateUrl: 'modules/medicalGuide/medicalGuide.html',
      controller: medicalGuideCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
