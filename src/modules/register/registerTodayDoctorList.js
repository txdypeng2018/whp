(function(app) {
  'use strict';

  var registerTodayDoctorListCtrl = function($scope, $http, $state, $stateParams, $filter) {
    //取得今天科室排班的医生列表
    var today = $filter('date')(new Date(),'yyyy-MM-dd');
    $http.get('/schedule/doctors', {params: {deptId: $stateParams.id, date: today}}).success(function(data) {
      $scope.doctors = data;
    });

    //医生选择事件
    $scope.doctorClk = function(id) {
      $state.go('registerConfirmToday', {doctorId: id, deptId: $stateParams.id});
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('registerTodayDoctorList', {
      url: '/register/registerTodayDoctorList/:id',
      cache: 'false',
      templateUrl: 'modules/register/registerTodayDoctorList.html',
      controller: registerTodayDoctorListCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
