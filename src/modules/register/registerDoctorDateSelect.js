(function(app) {
  'use strict';

  var registerDoctorDateSelectCtrl = function($scope, $http, $state, $stateParams, $filter, $timeout, ionicDatePicker) {
    var displayDays = 7;
    var daySelected = '';
    var weekStr = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];

    //取得指定天数以后的日期
    var getNextDay = function(date, days){
      date = +date + 1000*60*60*24*days;
      date = new Date(date);
      return date;
    };

    //取得科室下的医生
    var getDeptDoctors = function(){
      $http.get('/doctors', {params: {subjectId: $stateParams.subjectId}}).success(function(data) {
        $scope.doctors = data;
      });
    };

    //设置可选择的日期
    var setSelectDay = function(date) {
      return {
        date: $filter('date')(date, 'yyyy-MM-dd'),
        month: date.getMonth()+1,
        day: date.getDate(),
        week: weekStr[date.getDay()]
      };
    };

    //初始化可选择日期
    var selectDayInit = function(date) {
      daySelected = '';
      getDeptDoctors();

      var selectDays = [];
      for (var i = 0 ; i < displayDays ; i++) {
        if (i === 0) {
          selectDays[i] = setSelectDay(date);
        }
        else {
          date = getNextDay(new Date(selectDays[i-1].date), 1);
          selectDays[i] = setSelectDay(date);
        }
      }
      return selectDays;
    };
    $scope.selectDays = selectDayInit(getNextDay(new Date(), 1));

    //日期选择事件
    $scope.dayClk = function(index, date) {
      if (daySelected === date) {
        daySelected = '';
        angular.element(document.querySelectorAll('.day-select')[index]).removeClass('positive day-selected');
        getDeptDoctors();
      }
      else {
        daySelected = date;
        for (var m = 0 ; m < displayDays ; m++) {
          if (m !== index) {
            angular.element(document.querySelectorAll('.day-select')[m]).removeClass('positive day-selected');
          }
        }
        angular.element(document.querySelectorAll('.day-select')[index]).addClass('positive day-selected');
        $http.get('/schedule/doctors', {params: {date: daySelected}}).success(function(data) {
          $scope.doctors = data;
        });
      }
    };

    //日期插件选择
    var dayPicker1 = {
      callback: function (date) {
        $scope.selectDays = selectDayInit(new Date(date));
        daySelected = $filter('date')(date, 'yyyy-MM-dd');
        $timeout(function(){
          angular.element(document.querySelectorAll('.day-select')[0]).addClass('positive day-selected');
        });
      },
      from: getNextDay(new Date(), 1),
      to: getNextDay(new Date(), 60),
      inputDate: new Date(),
      howTodayButton: false
    };
    $scope.dayPicker = function() {
      ionicDatePicker.openDatePicker(dayPicker1);
    };

    //医生选中事件
    $scope.doctorClk = function(doctorId, deptId) {
      $state.go('registerDoctorTimeSelect', {deptId: deptId, doctorId: doctorId, date: daySelected});
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('registerDoctorDateSelect', {
      url: '/register/registerDoctorDateSelect/:id',
      cache: 'false',
      templateUrl: 'modules/register/registerDoctorDateSelect.html',
      controller: registerDoctorDateSelectCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
