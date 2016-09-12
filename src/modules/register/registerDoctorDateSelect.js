(function(app) {
  'use strict';

  var registerDoctorDateSelectCtrl = function($scope, $http, $state, $stateParams, $filter, $timeout, ionicDatePicker, $cordovaToast) {
    $scope.hideSearch = true;
    $scope.daySelected = '';
    //默认顶部栏选中为按时间选择
    $scope.districtId = '1';
    //是否隐藏时间栏
    $scope.dataPicker = {
        isShow:true
    };

    var displayDays = 7;
    var weekStr = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];

    //取得指定天数以后的日期
    var getNextDay = function(date, days){
      date = +date + 1000*60*60*24*days;
      date = new Date(date);
      return date;
    };

    //取得医生照片
    var getDoctorPhoto = function(doctorId, index) {
      $http.get('/doctors/photo', {params: {doctorId: doctorId, index: index}}).success(function(data, status, headers, config) {
        $scope.doctors[config.params.index].photo = data;
      }).error(function(data){
        $cordovaToast.showShortBottom(data);
      });
    };

    //取得科室下的医生
    $scope.major = $stateParams.major;
    var getDoctors = function(){
      var startDate = '';
      var endDate = '';
      if ($scope.daySelected !== '') {
        startDate = $scope.daySelected;
        endDate = $scope.daySelected;
      }
      else {
        startDate = $filter('date')(getNextDay(new Date(), 1), 'yyyy-MM-dd');
        endDate = $filter('date')(getNextDay(new Date(), 60), 'yyyy-MM-dd');
      }
      var params = {
        districtId: $stateParams.districtId,
        subjectId: $stateParams.subjectId,
        major: $scope.major,
        startDate: startDate,
        endDate: endDate
      };
      $http.get('/schedule/doctors', {params: params}).success(function(data) {
        $scope.doctors = data;
        for (var i = 0 ; i < data.length ; i++) {
          getDoctorPhoto(data[i].id, i);
        }
      }).error(function(data){
        $cordovaToast.showShortBottom(data);
      });
    };

    //医生方式选择事件
    $scope.districtClk = function(id) {
         $scope.districtId = id;
        if(id === '1'){
            $scope.dataPicker.isShow = true;
            $scope.daySelected = $filter('date')(getNextDay(new Date(), 1), 'yyyy-MM-dd');
        }
        if(id === '2'){
            $scope.dataPicker.isShow = false;
            $scope.daySelected = '';
        }
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
          if (date.getTime() > getNextDay(new Date(), 29).getTime()) {
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
    $scope.$on('$ionicView.beforeEnter', function(){
      getDoctors();
    });


    $scope.$on('$ionicView.beforeEnter', function(){
        //默认选中明天
        $scope.daySelected = $filter('date')(getNextDay(new Date(), 1), 'yyyy-MM-dd');
    });

    //日期选择事件
    $scope.dayClk = function(index, date) {
        $scope.daySelected = date;
        getDoctors();
    };

    //日期插件选择
    var dayPicker1 = {
      callback: function (date) {
        $scope.selectDays = selectDayInit(new Date(date));
        $scope.daySelected = $filter('date')(date, 'yyyy-MM-dd');
        getDoctors();
      },
      from: getNextDay(new Date(), 1),
      to: getNextDay(new Date(), 30),
      inputDate: new Date(),
      howTodayButton: false
    };
    $scope.dayPicker = function() {
      if ($scope.daySelected !== '') {
        dayPicker1.inputDate = new Date($scope.daySelected);
      }
      else {
        dayPicker1.inputDate = new Date();
      }
      ionicDatePicker.openDatePicker(dayPicker1);
    };

    //查询框显示隐藏事件
    $scope.searchClk = function() {
      $scope.hideSearch = !$scope.hideSearch;
      if (!$scope.hideSearch) {
        $timeout(function(){
          document.getElementById('registerDoctorDateSelect_search').focus();
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

    //医生选中事件
    $scope.doctorClk = function(doctorId, overCount) {
      if (overCount > 0) {
        //判断如果是选择医生状态，则置选择日期为空
        if(!$scope.dataPicker.isShow){
            $scope.daySelected = '';
        }
        $state.go('registerDoctorTimeSelect', {doctorId: doctorId, date: $scope.daySelected});
      }
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('registerDoctorDateSelect', {
      url: '/register/registerDoctorDateSelect/:districtId/:subjectId/:major',
      templateUrl: 'modules/register/registerDoctorDateSelect.html',
      controller: registerDoctorDateSelectCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
