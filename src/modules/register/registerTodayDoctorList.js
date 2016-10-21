(function(app) {
  'use strict';

  var registerTodayDoctorListCtrl = function($scope, $http, $state, $stateParams, $filter, $timeout, toastService, doctorPhotoService) {
    $scope.hideSearch = true;

    //取得医生照片
    var getDoctorPhoto = function(doctorId, index) {
      var image = doctorPhotoService.getPhoto(doctorId);
      if (!angular.isUndefined(image) && image !== '') {
        $scope.doctors[index].photo = image;
      }
      else {
        $http.get('/doctors/photo', {params: {doctorId: doctorId, index: index}}).success(function(data, status, headers, config) {
          $scope.doctors[config.params.index].photo = data;
          doctorPhotoService.setPhoto(doctorId, data);
        }).error(function(data, status, fun, config){
          $scope.doctors[config.params.index].photo = '';
        });
      }
    };

    //不同的院区的颜色
    $scope.districtColor = new Map();
    //颜色数组
    var color = ['district-icon-positive', 'district-icon-balanced',
      'district-icon-royal', 'district-icon-calm', 'district-icon-assertive'];
    //院区数量
    var districtCount = 0;
    //取得排班医生列表
    var today = $filter('date')(new Date(),'yyyy-MM-dd');
    $scope.major = $stateParams.major;
    $scope.majorTmp = $stateParams.major;
    var getDoctors = function(pageNo, isInit) {
      var params = {
        pageNo: pageNo,
        districtId: $stateParams.districtId,
        subjectId: $stateParams.subjectId,
        major: $scope.major,
        startDate: today,
        endDate: today,
        isAppointment: '0',
        index: $scope.httpIndex.index
      };
      $http.get('/schedule/doctors', {params: params}).success(function(data, status, headers, config) {
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
            for (var k = 0 ; k < data.length ; k++) {
              $scope.doctors.push(data[k]);
            }
          }
          var id;
          for (var i = index ; i < $scope.doctors.length ; i++) {
            $scope.doctors[i].district = $scope.doctors[i].district.substring(0,2);
            id = $scope.doctors[i].districtId;
            if (i > index) {
              if ($scope.doctors[i].districtId !== $scope.doctors[i - 1].districtId) {
                districtCount++;
                $scope.districtColor.set(id, color[districtCount - 1]);
              }
            } else {
              districtCount = 1;
              $scope.districtColor.set(id, color[districtCount - 1]);
            }
            getDoctorPhoto($scope.doctors[i].id, i);
          }
          $scope.$broadcast('scroll.infiniteScrollComplete');
        }
      }).error(function(data, status, fun, config){
        if (angular.isUndefined($scope.httpIndex[config.params.index])) {
          $scope.spinnerShow = false;
          $scope.doctors = [];
          toastService.show(data);
        }
      }).finally(function() {
        $scope.$broadcast('scroll.refreshComplete');
      });
    };

    $scope.$on('$ionicView.beforeEnter', function(){
      $scope.httpIndex = {index:1};
      //上拉加载医生
      $scope.vm = {
        moreData: true,
        pageNo: 1,
        init: function () {
          $scope.spinnerShow = true;
          $scope.doctors = null;
          $scope.vm.pageNo = 1;
          $scope.vm.moreData = true;
          getDoctors($scope.vm.pageNo, true);
        },
        loadMore: function () {
          $scope.vm.pageNo++;
          getDoctors($scope.vm.pageNo, false);
        }
      };
      $scope.vm.init();
    });

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
      if ((angular.isUndefined($stateParams.subjectId) || $stateParams.subjectId === '') && (angular.isUndefined($scope.major) || $scope.major === '')) {
        $scope.major = $scope.majorTmp;
        toastService.show('必须输入查询条件');
      }
      else {
        $scope.majorTmp = $scope.major;
        $scope.httpIndex.index++;
        $scope.vm.init();
      }
    };

    //选择照片事件
    $scope.photoClk = function(id, event) {
      event.stopPropagation();
      $state.go('doctorIntroductionView', {doctorId: id, type: '0'});
    };

    //医生选择事件
    $scope.doctorClk = function(doctorId, overCount, unallowed) {
      if ((angular.isUndefined(unallowed) || unallowed === null || unallowed === '') && overCount > 0) {
        $state.go('registerDoctorTimeSelect', {doctorId: doctorId, date: today, districtId: $stateParams.districtId, subjectId: $stateParams.subjectId,  type: '1'});
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
