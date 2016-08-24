(function(app) {
  'use strict';

  var medicalGuideCtrl = function($scope, $http, $ionicHistory) {
    //取得就医指南信息
    $http.get('/medicalGuide').success(function(data) {
      $scope.medicalGuide = data;
    });

    //返回上页
    $scope.goBack = function() {
      $ionicHistory.goBack();
    };
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
