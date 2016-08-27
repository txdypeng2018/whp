(function(app) {
  'use strict';

  var doctorIntroductionViewCtrl = function($scope, $http, $state, $stateParams, $cordovaToast) {
    $scope.type = ($stateParams.type==='1');

    //取得医生简介
    $http.get('/doctors/'+$stateParams.doctorId).success(function(data) {
      $scope.introduction = data;
    }).error(function(data){
      $cordovaToast.showShortBottom(data);
    });

    //挂号点击事件
    $scope.register = function() {
      $state.go('registerDoctorTimeSelect', {doctorId: $stateParams.doctorId, date: ''});
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('doctorIntroductionView', {
      url: '/doctorIntroductionList/doctorIntroductionView/:doctorId/:type',
      templateUrl: 'modules/doctorIntroduction/doctorIntroductionView.html',
      controller: doctorIntroductionViewCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
