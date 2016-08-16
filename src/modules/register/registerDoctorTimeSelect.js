(function(app) {
  'use strict';

  var registerDoctorTimeSelectCtrl = function($scope, $http, $state, $stateParams, $filter, $timeout, ionicDatePicker) {
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
    var getScheduleTimes = function(date) {
      $http.get('/schedule/times', {params: {deptId: $stateParams.deptId, doctorId: $stateParams.doctorId, date: date}}).success(function(data) {
        $scope.times = data;
      });
    };

    //取得医生信息
    $http.get('/doctors/'+$stateParams.doctorId).success(function(data) {
      $scope.doctor = data;
    });

    //取得科室信息
    $http.get('/organization/depts/'+$stateParams.deptId).success(function(data) {
      $scope.dept = data;
    });

    //设置可选择的日期
    var setSelectDay = function(date) {
      return {
        date: $filter('date')(date, 'yyyy-MM-dd'),
        month: date.getMonth()+1,
        day: date.getDate(),
        week: weekStr[date.getDay()]
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
    var daySelected = $stateParams.date;
    var dateTmp = null;
    if (daySelected !== null && daySelected !== '') {
      dateTmp = new Date(daySelected);
      $scope.allDays = [{date: daySelected}];
      $scope.selectDays[0] = setSelectDay(dateTmp);
      fillSelectDays();
      $timeout(function(){
        angular.element(document.querySelectorAll('.day-select')[0]).addClass('positive day-selected');
      }, 600);
      getScheduleTimes(daySelected);
    }
    else {
      $http.get('/schedule/dates', {params: {deptId: $stateParams.deptId, doctorId: $stateParams.doctorId}}).success(function(data) {
        $scope.allDays = data;
        for (var j = 0 ; j < data.length ; j++) {
          if (j === displayDays) {
            break;
          }
          dateTmp = new Date(data[j].date);
          $scope.selectDays[j] = setSelectDay(dateTmp);
        }
        if ($scope.selectDays.length < 7) {
          fillSelectDays();
        }
        if ($scope.selectDays.length > 0) {
          daySelected = data[0].date;
          $timeout(function(){
            angular.element(document.querySelectorAll('.day-select')[0]).addClass('positive day-selected');
          }, 600);
          getScheduleTimes(daySelected);
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
              daySelected = $filter('date')(date, 'yyyy-MM-dd');
              $scope.selectDays = [];
              var indexTmp = 0;
              for (var x = 0 ; x < $scope.allDays.length ; x++) {
                if ((indexTmp === 0 && daySelected === $scope.allDays[x].date) || indexTmp > 0) {
                  $scope.selectDays[indexTmp] = setSelectDay(new Date($scope.allDays[x].date));
                  indexTmp++;
                }
                if (indexTmp === displayDays) {
                  break;
                }
              }
              fillSelectDays();
              $timeout(function(){
                angular.element(document.querySelectorAll('.day-select')[0]).addClass('positive day-selected');
              });
            },
            from: new Date($scope.allDays[0].date),
            to: new Date($scope.allDays[$scope.allDays.length-1].date),
            inputDate: new Date(daySelected),
            disabledDates: disabledDates,
            howTodayButton: false
          };
        }
      });
    }

    //选择照片事件
    $scope.photoClk = function(id) {
      $state.go('doctorIntroductionView', {id: id});
    };

    //日期选择事件
    $scope.dayClk = function(index, date) {
      if (date !== '' && daySelected !== date) {
        daySelected = date;
        getScheduleTimes(daySelected);
        for (var m = 0 ; m < displayDays ; m++) {
          if (m !== index) {
            angular.element(document.querySelectorAll('.day-select')[m]).removeClass('positive day-selected');
          }
        }
        angular.element(document.querySelectorAll('.day-select')[index]).addClass('positive day-selected');
      }
    };

    //日期插件选择
    $scope.dayPicker = function() {
      ionicDatePicker.openDatePicker(dayPicker1);
    };

    //时间选中事件
    $scope.timeClk = function(time) {
      if (daySelected !== null && daySelected !== '') {
        $state.go('registerConfirmAppt', {doctorId: $stateParams.doctorId, deptId: $stateParams.deptId, date: daySelected+' '+time});
      }
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('registerDoctorTimeSelect', {
      url: '/register/registerDoctorTimeSelect/:deptId/:doctorId/:date',
      cache: 'false',
      templateUrl: 'modules/register/registerDoctorTimeSelect.html',
      controller: registerDoctorTimeSelectCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
