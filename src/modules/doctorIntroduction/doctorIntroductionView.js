(function(app) {
  'use strict';

  var doctorIntroductionViewCtrl = function($scope, $http, $stateParams) {
    //取得医生简介
    $http.get('/doctors/'+$stateParams.id).success(function(data) {
      $scope.introduction = data;
    });
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('doctorIntroductionView', {
      url: '/doctorIntroductionList/doctorIntroductionView/:id',
      templateUrl: 'modules/doctorIntroduction/doctorIntroductionView.html',
      controller: doctorIntroductionViewCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
