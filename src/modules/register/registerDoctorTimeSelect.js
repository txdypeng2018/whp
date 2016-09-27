(function(app) {
  'use strict';

  var registerDoctorTimeSelectCtrl = function($scope, $http, $state, $stateParams, $filter, $timeout, $cordovaToast, userService, doctorPhotoService) {
    //数据初始化
    var weekStr = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];

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
      $http.get('/schedule/times', {params: {doctorId: $stateParams.doctorId, date: date, districtId: $stateParams.districtId}}).success(function(data) {
        if (data.length%3 !== 0) {
          var count = 3 - data.length%3;
          for (var i = 0 ; i < count ; i++) {
            data.push({time:''});
          }
        }
        $scope.times = data;
      }).error(function(data){
        $cordovaToast.showShortBottom(data);
      });
    };

    //取得医生信息
    $http.get('/doctors/'+$stateParams.doctorId).success(function(data) {
      $scope.doctor = data;
      var image = doctorPhotoService.getPhoto($scope.doctor.id);
      if (!angular.isUndefined(image) && image !== '') {
        $scope.photo = image;
      }
      else {
        $http.get('/doctors/photo', {params: {doctorId: $scope.doctor.id}}).success(function(data) {
          $scope.photo = data;
          doctorPhotoService.setPhoto($scope.doctor.id, data);
        }).error(function(){
          $scope.photo = '';
        });
      }
    }).error(function(data){
      $cordovaToast.showShortBottom(data);
    });

    $scope.dateSelectParam = {
      selectDays: [],
      daySelected: $stateParams.date
    };
    $scope.dataInfos = {};
    $http.get('/schedule/dates', {params: {doctorId: $stateParams.doctorId, date: $scope.dateSelectParam.daySelected, districtId: $stateParams.districtId}}).success(function(data) {
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
      }
    }).error(function(data){
      $cordovaToast.showShortBottom(data);
    });

    $scope.$on('$ionicView.beforeEnter', function(){
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
      getScheduleTimes($scope.dateSelectParam.daySelected);
      $scope.dataInfo = $scope.dataInfos[$scope.dateSelectParam.daySelected];
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
              $cordovaToast.showShortBottom(data);
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
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('registerDoctorTimeSelect', {
      url: '/register/registerDoctorTimeSelect/:doctorId/:date/:districtId/:type',
      templateUrl: 'modules/register/registerDoctorTimeSelect.html',
      controller: registerDoctorTimeSelectCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
