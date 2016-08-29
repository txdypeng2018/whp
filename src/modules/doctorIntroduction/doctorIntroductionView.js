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

    //收藏医生
    $http.get('/user/collectionDoctors/'+$stateParams.doctorId).success(function(data) {
      if (angular.isUndefined(data.doctorId) || data.doctorId === '') {
        $scope.isCollection = false;
      }
      else {
        $scope.isCollection = true;
      }
    }).error(function(data){
      $cordovaToast.showShortBottom(data);
    });
    $scope.collectionDoctor = function() {
      if ($scope.isCollection) {
        $http.delete('/user/collectionDoctors/'+$stateParams.doctorId).success(function(data) {
          $scope.isCollection = false;
        }).error(function(data){
          $cordovaToast.showShortBottom(data);
        });
      }
      else {
        $http.post('/user/collectionDoctors', {doctorId: $stateParams.doctorId}).success(function(data) {
          $scope.isCollection = true;
        }).error(function(data){
          $cordovaToast.showShortBottom(data);
        });
      }

    };

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
