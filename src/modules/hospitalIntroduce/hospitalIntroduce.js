(function(app) {
  'use strict';

  var hospitalIntroduceCtrl = function($scope, $http) {
    //取得医院简介信息
    $http.get('/hospitalIntroduce').success(function(data) {
      $scope.hospitalIntroduce = data;
    });
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
