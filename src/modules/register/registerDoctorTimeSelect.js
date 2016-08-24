(function(app) {
  'use strict';

  var registerDoctorTimeSelectCtrl = function($scope, $http, $state, $stateParams, $filter, $timeout, ionicDatePicker, $ionicHistory) {
    var displayDays = 7;
    var weekStr = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
    $scope.selectDays = [];
    $scope.allDays = [];
    var dayPicker1 = {};

    //取得指定天数以后的日期
    var getNextDay = function(date, days){
      date = +date + 1000*60*60*24*days;
      date = new Date(date);
      return date;
    };

    //取得排班时间
    $scope.timeTypes = [];
    var getScheduleTimes = function(date) {
      $http.get('/schedule/times', {params: {doctorId: $stateParams.doctorId, date: date}}).success(function(data) {
        if (data.length%3 != 0) {
          var count = 3 - data.length%3;
          for (var i = 0 ; i < count ; i++) {
            data.push({time:''});
          }
        }
        $scope.times = data;
      });
    };

    //取得医生信息
    $http.get('/doctors/'+$stateParams.doctorId).success(function(data) {
      $scope.doctor = data;
      $http.get('/doctors/photo', {params: {doctorId: $scope.doctor.id}}).success(function(data) {
        $scope.photo = data;
      });
    });

    //取得科室信息
    $http.get('/organization/depts/'+$stateParams.deptId).success(function(data) {
      $scope.dept = data;
    });

    //设置可选择的日期
    var setSelectDay = function(date, data) {
      return {
        date: $filter('date')(date, 'yyyy-MM-dd'),
        month: date.getMonth()+1,
        day: date.getDate(),
        week: weekStr[date.getDay()],
        data: data
      };
    };

    //填充满时间栏
    var fillSelectDays = function() {
      for (var i = $scope.selectDays.length; i < displayDays ; i++) {
        $scope.selectDays[i] = {
          date: '',
          month: '',
          day: '',
          week: ''
        };
      }
    };

    //取得医生排班时间
    $scope.daySelected = $stateParams.date;
    $scope.dataSelected = {};
    var dateTmp = null;
    if ($scope.daySelected !== null && $scope.daySelected !== '') {
      $http.get('/schedule/dates', {params: {doctorId: $stateParams.doctorId, date: $scope.daySelected}}).success(function(data) {
        dateTmp = new Date($scope.daySelected);
        $scope.allDays = [{date: $scope.daySelected, data: data[0]}];
        $scope.selectDays[0] = setSelectDay(dateTmp, data[0]);
        fillSelectDays();
        $scope.dataSelected = $scope.allDays[0].data;
        getScheduleTimes($scope.daySelected);
      });
    }
    else {
      $http.get('/schedule/dates', {params: {doctorId: $stateParams.doctorId}}).success(function(data) {
        $scope.allDays = data;
        for (var j = 0 ; j < data.length ; j++) {
          if (j === displayDays) {
            break;
          }
          dateTmp = new Date(data[j].date);
          $scope.selectDays[j] = setSelectDay(dateTmp, data[j]);
        }
        if ($scope.selectDays.length < 7) {
          fillSelectDays();
        }
        if ($scope.selectDays.length > 0) {
          $scope.daySelected = data[0].date;
          $scope.dataSelected = data[0];
          getScheduleTimes($scope.daySelected);
        }

        //初始化日期选择控件
        if ($scope.allDays.length > 7) {
          var disabledDates = [];
          var indexTmp = 0;
          dateTmp = new Date($scope.allDays[0].date);
          while (true) {
            if (dateTmp.getTime() !== new Date($scope.allDays[indexTmp].date).getTime()) {
              disabledDates.push(dateTmp);
            }
            else {
              indexTmp++;
            }
            dateTmp = getNextDay(dateTmp, 1);
            if (indexTmp === $scope.allDays.length - 1) {
              break;
            }
          }

          dayPicker1 = {
            callback: function (date) {
              $scope.daySelected = $filter('date')(date, 'yyyy-MM-dd');
              $scope.selectDays = [];
              var indexTmp = 0;
              for (var x = 0 ; x < $scope.allDays.length ; x++) {
                if ((indexTmp === 0 && $scope.daySelected === $scope.allDays[x].date) || indexTmp > 0) {
                  $scope.selectDays[indexTmp] = setSelectDay(new Date($scope.allDays[x].date), $scope.allDays[x]);
                  indexTmp++;
                }
                if (indexTmp === displayDays) {
                  break;
                }
              }
              fillSelectDays();
              $scope.dataSelected = $scope.selectDays[0].data;
            },
            from: new Date($scope.allDays[0].date),
            to: new Date($scope.allDays[$scope.allDays.length-1].date),
            inputDate: new Date($scope.daySelected),
            disabledDates: disabledDates,
            howTodayButton: false
          };
        }
      });
    }

    //返回上页
    $scope.goBack = function() {
      $ionicHistory.goBack();
    };

    //选择照片事件
    $scope.photoClk = function(id) {
      $state.go('doctorIntroductionView', {id: id});
    };

    //日期选择事件
    $scope.dayClk = function(index, date) {
      if (date !== '' && $scope.daySelected !== date) {
        $scope.daySelected = date;
        getScheduleTimes($scope.daySelected);
        $scope.dataSelected = $scope.selectDays[index].data;
      }
    };

    //日期插件选择
    $scope.dayPicker = function() {
      dayPicker1.inputDate = new Date($scope.daySelected);
      ionicDatePicker.openDatePicker(dayPicker1);
    };

    //时间选中事件
    $scope.timeClk = function(time, overCount) {
      if ($scope.daySelected !== null && $scope.daySelected !== '' && overCount > 0) {
        $state.go('registerConfirmAppt', {doctorId: $stateParams.doctorId, date: $scope.daySelected+' '+time});
      }
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('registerDoctorTimeSelect', {
      url: '/register/registerDoctorTimeSelect/:doctorId/:date',
      templateUrl: 'modules/register/registerDoctorTimeSelect.html',
      controller: registerDoctorTimeSelectCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
