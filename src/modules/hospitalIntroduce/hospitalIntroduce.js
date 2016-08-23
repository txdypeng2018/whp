(function(app) {
  'use strict';

  var hospitalIntroduceCtrl = function($scope, $http, $ionicHistory) {
    //取得医院简介信息
    $http.get('/hospitalIntroduce').success(function(data) {
      $scope.hospitalIntroduce = data;
    });

    //返回上页
    $scope.goBack = function() {
      $ionicHistory.goBack();
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('hospitalIntroduce', {
      url: '/hospitalIntroduce',
      templateUrl: 'modules/hospitalIntroduce/hospitalIntroduce.html',
      controller: hospitalIntroduceCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
