(function(app) {
  'use strict';

  var registerDoctorTimeSelectCtrl = function($scope, $http, $state, $stateParams, $filter, $timeout, $ionicScrollDelegate, toastService, userService, doctorPhotoService, utilsService) {
    //数据初始化
    var weekStr = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
    var isAppointment = '1';
    if ($stateParams.type === '1') {
      isAppointment = '0';
    }

    //设置院区颜色
    $http.get('/organization/districts').success(function(data) {
      $scope.districtColor = utilsService.getDistrictColor(data);
    });

    //设置可选择的日期
    var setSelectDay = function(date) {
      return {
        date: $filter('date')(date, 'yyyy-MM-dd'),
        month: date.getMonth() + 1,
        day: date.getDate(),
        week: weekStr[date.getDay()]
      };
    };

    //取得排班时间
    var getScheduleTimes = function(date) {
      $scope.times = [];
      $ionicScrollDelegate.$getByHandle('registerTimeScroll').scrollTop();
      $http.get('/schedule/times', {params: {doctorId: $stateParams.doctorId, date: date, districtId: $stateParams.districtId, isAppointment: isAppointment, index: $scope.httpIndex.index}}).success(function(data, status, headers, config) {
        if (angular.isUndefined($scope.httpIndex[config.params.index])) {
          if (data.length%3 !== 0) {
            var count = 3 - data.length%3;
            for (var i = 0 ; i < count ; i++) {
              data.push({time:''});
            }
          }
          $scope.times = data;
        }
      }).error(function(data, status, fun, config){
        if (angular.isUndefined($scope.httpIndex[config.params.index])) {
          toastService.show(data);
        }
      }).finally(function() {
        $scope.$broadcast('scroll.refreshComplete');
      });
    };

    //取得医生信息
    $scope.doctorId = $stateParams.doctorId;
    var image = doctorPhotoService.getPhoto($stateParams.doctorId);
    if (!angular.isUndefined(image) && image !== '') {
      $scope.photo = image;
    }
    else {
      $http.get('/doctors/photo', {params: {doctorId: $stateParams.doctorId}}).success(function(data) {
        $scope.photo = data;
        doctorPhotoService.setPhoto($stateParams.doctorId, data);
      }).error(function(){
        $scope.photo = '';
      });
    }

    $scope.dateSelectParam = {
      selectDays: [],
      daySelected: $stateParams.date
    };
    $scope.dataInfos = {};
    $http.get('/schedule/dates', {params: {doctorId: $stateParams.doctorId, date: $scope.dateSelectParam.daySelected, districtId: $stateParams.districtId, subjectId: $stateParams.subjectId, isAppointment: isAppointment}}).success(function(data) {
      if (data.length > 0) {
        var selectDays = [];
        for (var i = 0 ; i < data.length ; i++) {
          selectDays.push(setSelectDay(new Date(data[i].date)));
          $scope.dataInfos[data[i].date] = data[i];
        }
        $scope.dateSelectParam = {
          selectDays: selectDays,
          daySelected: data[0].date
        };
        getScheduleTimes($scope.dateSelectParam.daySelected);
        $scope.dataInfo = data[0];
        if ($scope.dataInfo.district.length > 2) {
          $scope.dataInfo.district = $scope.dataInfo.district.substring(0, 2);
        }
      }
    }).error(function(data){
      toastService.show(data);
    });

    $scope.$on('$ionicView.beforeEnter', function(){
      $scope.httpIndex = {index:1};
      if ($scope.dateSelectParam.selectDays.length > 0) {
        getScheduleTimes($scope.dateSelectParam.daySelected);
      }
    });

    //选择照片事件
    $scope.photoClk = function(id) {
      $state.go('doctorIntroductionView', {doctorId: id, type: '0'});
    };

    //日期选择事件
    $scope.dateSelectFun = function() {
      $scope.httpIndex.index++;
      getScheduleTimes($scope.dateSelectParam.daySelected);
      $scope.dataInfo = $scope.dataInfos[$scope.dateSelectParam.daySelected];
      if ($scope.dataInfo.district.length > 2) {
        $scope.dataInfo.district = $scope.dataInfo.district.substring(0, 2);
      }
    };

    //时间选中事件
    $scope.timeClk = function(time, overCount) {
      if ($scope.dateSelectParam.daySelected !== null && $scope.dateSelectParam.daySelected !== '' && overCount > 0) {
        var isLogin = userService.hasToken();
        if (isLogin) {
          $http.get('/user/tokenVal').success(function() {
            if ($stateParams.type === '1') {
              $state.go('registerConfirmToday', {doctorId: $stateParams.doctorId, date: $scope.dateSelectParam.daySelected+' '+time});
            }
            else {
              $state.go('registerConfirmAppt', {doctorId: $stateParams.doctorId, date: $scope.dateSelectParam.daySelected+' '+time});
            }
          }).error(function(data, status){
            if (status !== 401) {
              toastService.show(data);
            }
            else {
              userService.clearToken();
              $state.go('login');
            }
          });
        }
        else {
          $state.go('login');
        }
      }
    };

    //遮蔽罩取消
    $scope.spinnerCancel = function() {
      $scope.httpIndex[$scope.httpIndex.index] = 'CANCEL';
      $scope.$broadcast('scroll.refreshComplete');
    };

    //下拉刷新
    $scope.doRefresh = function() {
      $scope.httpIndex.index++;
      getScheduleTimes($scope.dateSelectParam.daySelected);
    };

    //返回首页
    $scope.goMainPage = function() {
      $state.go('tab.main');
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('registerDoctorTimeSelect', {
      url: '/register/registerDoctorTimeSelect/:doctorId/:date/:districtId/:subjectId/:type',
      templateUrl: 'modules/register/registerDoctorTimeSelect.html',
      controller: registerDoctorTimeSelectCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
