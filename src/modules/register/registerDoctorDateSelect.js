(function (app) {
  'use strict';

  var registerDoctorDateSelectCtrl = function ($scope, $http, $state, $stateParams, $filter, $timeout, toastService, doctorPhotoService) {
    //数据初始化
    var color = ['district-icon-positive', 'district-icon-balanced',
      'district-icon-royal', 'district-icon-calm', 'district-icon-assertive'];
    var weekStr = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
    var displayDays = 30;
    $scope.districtColor = {};
    var districtCount = 0;
    $scope.major = $stateParams.major;
    $scope.majorTmp = $stateParams.major;
    $scope.hasSearchStr = (!angular.isUndefined($scope.major) && $scope.major !== '');
    //默认选中预约时间方式
    $scope.appointmentMode = '1';
    //默认隐藏搜索栏
    $scope.hideSearch = true;

    //取得指定天数以后的日期
    var getNextDay = function (date, days) {
      date = +date + 1000 * 60 * 60 * 24 * days;
      date = new Date(date);
      return date;
    };

    //设置院区颜色
    var setDistrictColor = function(districtId) {
      if (angular.isUndefined($scope.districtColor[districtId])) {
        $scope.districtColor[districtId] = color[districtCount];
        districtCount++;
      }
    };

    //取得医生照片
    var getDoctorPhoto = function (doctorId, index) {
      var image = doctorPhotoService.getPhoto(doctorId);
      if (!angular.isUndefined(image) && image !== '') {
        $scope.doctors[index].photo = image;
      }
      else {
        $http.get('/doctors/photo', {params: {doctorId: doctorId, index: index}}).success(function (data, status, headers, config) {
          $scope.doctors[config.params.index].photo = data;
          doctorPhotoService.setPhoto(doctorId, data);
        }).error(function (data, status, fun, config) {
          $scope.doctors[config.params.index].photo = '';
        });
      }
    };

    //取得医生列表
    var getDoctors = function (pageNo, isInit) {
      var startDate = '';
      var endDate = '';
      if ($scope.dateSelectParam.daySelected !== '' && !$scope.hasSearchStr && $scope.appointmentMode === '1') {
        startDate = $scope.dateSelectParam.daySelected;
        endDate = $scope.dateSelectParam.daySelected;
      }
      else {
        startDate = $filter('date')(getNextDay(new Date(), 1), 'yyyy-MM-dd');
        endDate = $filter('date')(getNextDay(new Date(), displayDays), 'yyyy-MM-dd');
      }
      var params = {
        pageNo: pageNo,
        districtId: $stateParams.districtId,
        subjectId: $stateParams.subjectId,
        major: $scope.major,
        startDate: startDate,
        endDate: endDate,
        isAppointment: '1',
        index: $scope.httpIndex.index
      };
      $http.get('/schedule/doctors', {params: params}).success(function (data, status, headers, config) {
        if (angular.isUndefined($scope.httpIndex[config.params.index])) {
          $scope.spinnerShow = false;
          if (data.length === 0) {
            $scope.vm.moreData = false;
          }
          var index = 0;
          if (isInit) {
            $scope.doctors = data;
          }
          else {
            index = $scope.doctors.length;
            for (var k = 0; k < data.length; k++) {
              $scope.doctors.push(data[k]);
            }
          }
          for (var i = index; i < $scope.doctors.length; i++) {
            $scope.doctors[i].district = $scope.doctors[i].district.substring(0,2);
            setDistrictColor($scope.doctors[i].districtId);
            getDoctorPhoto($scope.doctors[i].id, i);
          }
          $scope.$broadcast('scroll.infiniteScrollComplete');
        }
      }).error(function (data, status, fun, config) {
        if (angular.isUndefined($scope.httpIndex[config.params.index])) {
          $scope.spinnerShow = false;
          $scope.doctors = [];
          toastService.show(data);
        }
      }).finally(function() {
        $scope.$broadcast('scroll.refreshComplete');
      });
    };

    //设置可选择的日期
    var setSelectDay = function (date) {
      return {
        date: $filter('date')(date, 'yyyy-MM-dd'),
        month: date.getMonth() + 1,
        day: date.getDate(),
        week: weekStr[date.getDay()]
      };
    };

    //初始化可选择日期
    var selectDayInit = function (date) {
      var dateTmp = date;
      var selectDays = [];
      var i = 0;
      while (true) {
        if (dateTmp.getTime() > getNextDay(new Date(), displayDays).getTime()) {
          break;
        }
        selectDays[i] = setSelectDay(dateTmp);
        dateTmp = getNextDay(dateTmp, 1);
        i++;
      }
      return selectDays;
    };

    //上拉加载医生
    $scope.vm = {
      moreData: true,
      pageNo: 1,
      init: function () {
        $scope.spinnerShow = true;
        $scope.doctors = null;
        $scope.vm.pageNo = 1;
        getDoctors($scope.vm.pageNo, true);
      },
      loadMore: function () {
        $scope.vm.pageNo++;
        getDoctors($scope.vm.pageNo, false);
      }
    };

    $scope.dateSelectParam = {
      selectDays: selectDayInit(getNextDay(new Date(), 1)),
      daySelected: $filter('date')(getNextDay(new Date(), 1), 'yyyy-MM-dd')
    };

    $scope.$on('$ionicView.beforeEnter', function () {
      $scope.httpIndex = {index:1};
      $scope.vm.init();
    });

    //日期选中事件
    $scope.dateSelectFun = function() {
      $scope.httpIndex.index++;
      $scope.vm.init();
    };

    //预约选择方式切换事件
    $scope.appointmentModeClk = function (id) {
      $scope.appointmentMode = id;
      $scope.httpIndex.index++;
      $scope.vm.init();
    };

    //查询框显示隐藏事件
    $scope.searchClk = function () {
      $scope.hideSearch = !$scope.hideSearch;
      if (!$scope.hideSearch) {
        $timeout(function () {
          document.getElementById('registerDoctorDateSelect_search').focus();
        }, 50);
      }
    };
    //查询事件
    $scope.doSearch = function () {
      $scope.hasSearchStr = (!angular.isUndefined($scope.major) && $scope.major !== '');
      $scope.majorTmp = $scope.major;
      $scope.httpIndex.index++;
      $scope.vm.init();
    };

    //选择照片事件
    $scope.photoClk = function (id, event) {
      event.stopPropagation();
      $state.go('doctorIntroductionView', {doctorId: id, type: '0'});
    };

    //医生选中事件
    $scope.doctorClk = function (doctorId, overCount) {
      if (overCount > 0) {
        //判断如果是选择医生状态，则置选择日期为空
        var date = $scope.dateSelectParam.daySelected;
        if ($scope.appointmentMode === '2' || $scope.hasSearchStr) {
          date = '';
        }
        $state.go('registerDoctorTimeSelect', {doctorId: doctorId, date: date, districtId: $stateParams.districtId, subjectId: $stateParams.subjectId, type: '2'});
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
      $scope.vm.init();
    };

    $scope.contentMarginTop = function() {
      if (!$scope.hasSearchStr) {
        if ($scope.appointmentMode === '1') {
          return 'registerDoctorDateSelect-top1';
        }
        else {
          return 'registerDoctorDateSelect-top2';
        }
      }
      else {
        return '';
      }
    };
  };

  var mainRouter = function ($stateProvider) {
    $stateProvider.state('registerDoctorDateSelect', {
      url: '/register/registerDoctorDateSelect/:districtId/:subjectId/:major',
      templateUrl: 'modules/register/registerDoctorDateSelect.html',
      controller: registerDoctorDateSelectCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
