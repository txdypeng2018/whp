(function(app) {
  'use strict';

  var doctorIntroductionViewCtrl = function($scope, $http, $state, $stateParams, $cordovaToast, userService) {
    $scope.type = ($stateParams.type==='1');
    var isLogin = userService.hasToken();
    $http.get('/user/tokenVal').error(function(data, status){
      if (status === 401) {
        userService.clearToken();
        isLogin = false;
      }
    });

    //取得医生简介
    $http.get('/doctors/'+$stateParams.doctorId).success(function(data) {
      $scope.introduction = data;
      $http.get('/doctors/photo', {params: {doctorId: $stateParams.doctorId}}).success(function(data) {
        $scope.introduction.photo = data;
      }).error(function(){
        $scope.introduction.photo = '';
      });
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
    }).error(function(data, status){
      if (status === 401) {
        $scope.isCollection = false;
      }
      else {
        $cordovaToast.showShortBottom(data);
      }
    });
    $scope.collectionDoctor = function() {
      if (isLogin) {
        if ($scope.isCollection) {
          $http.delete('/user/collectionDoctors/'+$stateParams.doctorId).success(function() {
            $scope.isCollection = false;
          }).error(function(data){
            $cordovaToast.showShortBottom(data);
          });
        }
        else {
          $http.post('/user/collectionDoctors', {doctorId: $stateParams.doctorId}).success(function() {
            $scope.isCollection = true;
          }).error(function(data){
            $cordovaToast.showShortBottom(data);
          });
        }
      }
      else {
        $state.go('login');
      }
    };

    //挂号点击事件
    $scope.register = function() {
      if (isLogin) {
        $state.go('registerDoctorTimeSelect', {doctorId: $stateParams.doctorId, date: ''});
      }
      else {
        $state.go('login');
      }
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
