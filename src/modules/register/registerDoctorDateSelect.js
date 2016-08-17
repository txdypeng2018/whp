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

    $scope.major = $stateParams.major;
    if (angular.isUndefined($stateParams.subjectId) || $stateParams.subjectId === '') {
      $scope.isChild = '';
    }
    else {
      getSubjectInfo($stateParams.subjectId);
    }

    //取得科室下的医生
    var getDoctors = function(){
      var startDate = '';
      var endDate = '';
      if (daySelected !== '') {
        startDate = daySelected;
        endDate = daySelected;
      }
      else {
        startDate = $filter('date')(getNextDay(new Date(), 1), 'yyyy-MM-dd');
        endDate = $filter('date')(getNextDay(new Date(), 60), 'yyyy-MM-dd');
      }
      var params = {
        districtId: $stateParams.districtId,
        subjectId: $stateParams.subjectId,
        isChild: $scope.isChild,
        major: $scope.major,
        startDate: startDate,
        endDate: endDate
      };
      $http.get('/schedule/doctors', {params: params}).success(function(data) {
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
      var selectDays = [];
      for (var i = 0 ; i < displayDays ; i++) {
        if (i === 0) {
          selectDays[i] = setSelectDay(date);
        }
        else {
          if (date.getTime() > getNextDay(new Date(), 59).getTime()) {
            break;
          }
          date = getNextDay(new Date(selectDays[i-1].date), 1);
          selectDays[i] = setSelectDay(date);
        }
      }
      if (selectDays.length < displayDays) {
        var selectDayTmps = [];
        for (var j = 0 ; j < displayDays-selectDays.length ; j++) {
          selectDayTmps[j] = setSelectDay(getNextDay(new Date(selectDays[0].date), selectDays.length-displayDays+j));
        }
        var m = 0;
        for (var n = displayDays-selectDays.length ; n < displayDays ; n++) {
          selectDayTmps[n] = selectDays[m];
          m++;
        }
        selectDays = selectDayTmps;
      }
      return selectDays;
    };
    $scope.selectDays = selectDayInit(getNextDay(new Date(), 1));
    getDoctors();

    //日期选择事件
    $scope.dayClk = function(index, date) {
      if (daySelected === date) {
        daySelected = '';
        angular.element(document.querySelectorAll('.day-select')[index]).removeClass('positive day-selected');
        getDoctors();
      }
      else {
        daySelected = date;
        for (var m = 0 ; m < displayDays ; m++) {
          if (m !== index) {
            angular.element(document.querySelectorAll('.day-select')[m]).removeClass('positive day-selected');
          }
        }
        angular.element(document.querySelectorAll('.day-select')[index]).addClass('positive day-selected');
        getDoctors();
      }
    };

    //日期插件选择
    var dayPicker1 = {
      callback: function (date) {
        $scope.selectDays = selectDayInit(new Date(date));
        daySelected = $filter('date')(date, 'yyyy-MM-dd');
        getDoctors();
        $timeout(function(){
          for (var i = 0 ; i < displayDays ; i++) {
            if (daySelected === $scope.selectDays[i].date) {
              angular.element(document.querySelectorAll('.day-select')[i]).addClass('positive day-selected');
            }
          }
        });
      },
      from: getNextDay(new Date(), 1),
      to: getNextDay(new Date(), 60),
      inputDate: new Date(),
      howTodayButton: false
    };
    $scope.dayPicker = function() {
      if (daySelected !== '') {
        dayPicker1.inputDate = new Date(daySelected);
      }
      else {
        dayPicker1.inputDate = new Date();
      }
      ionicDatePicker.openDatePicker(dayPicker1);
    };

    //查询框显示隐藏事件
    $scope.searchClk = function() {
      if (angular.element(document.querySelector('.head-search')).hasClass('search-none')) {
        angular.element(document.querySelector('.head-search')).removeClass('search-none');
        $timeout(function(){
          document.getElementById('registerDoctorDateSelect_search').focus();
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

    //医生选中事件
    $scope.doctorClk = function(doctorId) {
      $state.go('registerDoctorTimeSelect', {doctorId: doctorId, date: daySelected});
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('registerDoctorDateSelect', {
      url: '/register/registerDoctorDateSelect/:districtId/:subjectId/:major',
      cache: 'false',
      templateUrl: 'modules/register/registerDoctorDateSelect.html',
      controller: registerDoctorDateSelectCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
