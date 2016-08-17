(function(app) {
  'use strict';

  var registerTodayDoctorListCtrl = function($scope, $http, $state, $stateParams, $filter, $timeout) {
    //取得学科信息
    var getSubjectInfo = function(subjectId) {
      $http.get('/subjects/'+subjectId).success(function(data) {
        $scope.subjectInfo = data;
        if (data.hasChild === '1') {
          $scope.isChild = '0';
        }
        else {
          $scope.isChild = '';
        }
      });
    };

    //取得排班医生列表
    var today = $filter('date')(new Date(),'yyyy-MM-dd');
    $scope.major = $stateParams.major;
    if (angular.isUndefined($stateParams.subjectId) || $stateParams.subjectId === '') {
      $scope.isChild = '';
    }
    else {
      getSubjectInfo($stateParams.subjectId);
    }
    var getDoctors = function() {
      var params = {
        districtId: $stateParams.districtId,
        subjectId: $stateParams.subjectId,
        isChild: $scope.isChild,
        major: $scope.major,
        startDate: today,
        endDate: today
      };
      $http.get('/schedule/doctors', {params: params}).success(function(data) {
        $scope.doctors = data;
      });
    };
    getDoctors();

    //查询框显示隐藏事件
    $scope.searchClk = function() {
      if (angular.element(document.querySelector('.head-search')).hasClass('search-none')) {
        angular.element(document.querySelector('.head-search')).removeClass('search-none');
        $timeout(function(){
          document.getElementById('registerTodayDoctorList_search').focus();
        });
      }
      else {
        angular.element(document.querySelector('.head-search')).addClass('search-none');
      }
    };

    //查询事件
    $scope.doSearch = function() {
      getDoctors();
    };

    //选择儿科事件
    $scope.isChildClk = function() {
      if($scope.isChild === '1') {
        $scope.isChild = '0';
      }
      else {
        $scope.isChild = '1';
      }
      getDoctors();
    };

    //选择照片事件
    $scope.photoClk = function(id, event) {
      event.stopPropagation();
      $state.go('doctorIntroductionView', {id: id});
    };

    //医生选择事件
    $scope.doctorClk = function(doctorId) {
      $state.go('registerConfirmToday', {doctorId: doctorId});
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('registerTodayDoctorList', {
      url: '/register/registerTodayDoctorList/:districtId/:subjectId/:major',
      cache: 'false',
      templateUrl: 'modules/register/registerTodayDoctorList.html',
      controller: registerTodayDoctorListCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
