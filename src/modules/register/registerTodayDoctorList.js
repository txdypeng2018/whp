(function(app) {
  'use strict';

  var registerTodayDoctorListCtrl = function($scope, $http, $state, $stateParams, $filter, $timeout) {
    $scope.hideSearch = true;

    //取得医生照片
    var getDoctorPhoto = function(doctorId, index) {
      $http.get('/doctors/photo', {params: {doctorId: doctorId, index: index}}).success(function(data, status, headers, config) {
        $scope.doctors[config.params.index].photo = data;
      });
    };

    //取得排班医生列表
    var today = $filter('date')(new Date(),'yyyy-MM-dd');
    $scope.major = $stateParams.major;
    var getDoctors = function() {
      var params = {
        districtId: $stateParams.districtId,
        subjectId: $stateParams.subjectId,
        major: $scope.major,
        startDate: today,
        endDate: today
      };
      $http.get('/schedule/doctors', {params: params}).success(function(data) {
        $scope.doctors = data;
        for (var i = 0 ; i < data.length ; i++) {
          getDoctorPhoto(data[i].id, i);
        }
      });
    };
    getDoctors();

    //查询框显示隐藏事件
    $scope.searchClk = function() {
      $scope.hideSearch = !$scope.hideSearch;
      if (!$scope.hideSearch) {
        $timeout(function(){
          document.getElementById('registerTodayDoctorList_search').focus();
        }, 50);
      }
    };

    //查询事件
    $scope.doSearch = function() {
      getDoctors();
    };

    //选择照片事件
    $scope.photoClk = function(id, event) {
      event.stopPropagation();
      $state.go('doctorIntroductionView', {doctorId: id, type: '0'});
    };

    //医生选择事件
    $scope.doctorClk = function(doctorId, overCount) {
      if (overCount > 0) {
        $state.go('registerConfirmToday', {doctorId: doctorId});
      }
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('registerTodayDoctorList', {
      url: '/register/registerTodayDoctorList/:districtId/:subjectId/:major',
      templateUrl: 'modules/register/registerTodayDoctorList.html',
      controller: registerTodayDoctorListCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
