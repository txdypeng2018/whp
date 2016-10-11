(function(app) {
  'use strict';

  var doctorIntroductionViewCtrl = function($scope, $http, $state, $stateParams, $cordovaToast, userService, doctorPhotoService) {
    $scope.type = ($stateParams.type==='1');
    $scope.$on('$ionicView.beforeEnter', function(){
      $scope.isLogin = userService.hasToken();
      $scope.isCollection = false;
      $http.get('/user/collectionDoctors/'+$stateParams.doctorId).success(function(data) {
        $scope.isCollection = !(angular.isUndefined(data.doctorId) || data.doctorId === '');
      }).error(function(data, status){
        if (status !== 401) {
          $cordovaToast.showShortBottom(data);
        }
        else {
          userService.clearToken();
          $scope.isLogin = false;
        }
      });
    });

    //取得医生简介
    $http.get('/doctors/'+$stateParams.doctorId).success(function(data) {
      $scope.introduction = data;
      var image = doctorPhotoService.getPhoto($stateParams.doctorId);
      if (!angular.isUndefined(image) && image !== '') {
        $scope.introduction.photo = image;
      }
      else {
        $http.get('/doctors/photo', {params: {doctorId: $stateParams.doctorId}}).success(function(data) {
          $scope.introduction.photo = data;
          doctorPhotoService.setPhoto($stateParams.doctorId, data);
        }).error(function(){
          $scope.introduction.photo = '';
        });
      }
    }).error(function(data){
      $cordovaToast.showShortBottom(data);
    });

    //收藏医生事件
    $scope.collectionDoctor = function() {
      if ($scope.isLogin) {
        if ($scope.isCollection) {
          $http.delete('/user/collectionDoctors/'+$stateParams.doctorId).success(function() {
            $scope.isCollection = false;
            $cordovaToast.showShortBottom('取消收藏成功');
          }).error(function(data){
            $cordovaToast.showShortBottom(data);
          });
        }
        else {
          $http.post('/user/collectionDoctors', {doctorId: $stateParams.doctorId}).success(function() {
            $scope.isCollection = true;
            $cordovaToast.showShortBottom('收藏成功');
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
      if ($scope.isLogin) {
        $state.go('registerDoctorTimeSelect', {doctorId: $stateParams.doctorId, date: '', districtId: '', type: '2'});
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