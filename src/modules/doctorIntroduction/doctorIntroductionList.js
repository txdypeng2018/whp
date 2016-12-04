(function(app) {
  'use strict';

  var doctorIntroductionListCtrl = function($scope, $http, $state, $timeout, $ionicHistory, doctorPhotoService, toastService, $ionicScrollDelegate) {
    $scope.title = '医生介绍';
    $scope.searchNameTmp = '';

    //取得医生照片
    var getDoctorPhoto = function(doctorId, index) {
      var image = doctorPhotoService.getPhoto(doctorId);
      if (!angular.isUndefined(image) && image !== '') {
        $scope.introductions[index].photo = image;
      }
      else {
        $http.get('/doctors/photo', {params: {doctorId: doctorId, index: index}}).success(function(data, status, headers, config) {
          $scope.introductions[config.params.index].photo = data;
          doctorPhotoService.setPhoto(doctorId, data);
        }).error(function(data, status, fun, config){
          $scope.introductions[config.params.index].photo = '';
        });
      }
    };

    //取得医生介绍列表
    var getDoctorIntroductions = function(param, isInit) {
      param.index = $scope.httpIndex.index;
      $http.get('/doctors', {params: param}).success(function(data, status, headers, config) {
        if (angular.isUndefined($scope.httpIndex[config.params.index])) {
          $scope.refreshFlg = false;
          $scope.spinnerShow = false;
          if(data.length < 10){
            $scope.vm.moreData = false;
          }
          var index = 0;
          if (isInit) {
            $scope.introductions = data;
          }
          else {
            index = $scope.introductions.length;
            for (var i = 0 ; i < data.length ; i++) {
              $scope.introductions.push(data[i]);
            }
          }
          for (var j = index ; j < $scope.introductions.length ; j++) {
            getDoctorPhoto($scope.introductions[j].id, j);
          }
        }
        else {
          $scope.vm.moreData = false;
        }
        $scope.$broadcast('scroll.infiniteScrollComplete');
      }).error(function(data, status, fun, config){
        if (angular.isUndefined($scope.httpIndex[config.params.index])) {
          $scope.spinnerShow = false;
          $scope.introductions = [];
          toastService.show(data);
        }
      }).finally(function() {
        $scope.$broadcast('scroll.refreshComplete');
      });
    };

    //搜索医生事件
    $scope.doSearch = function() {
      $scope.httpIndex.index++;
      $scope.searchNameTmp = $scope.searchName;
      $ionicScrollDelegate.scrollTop();
      $scope.vm.init();
    };

    //查看医生介绍详细
    $scope.viewIntroduction = function(id) {
      $scope.vm.moreData = false;
      $state.go('doctorIntroductionView', {doctorId: id, type: '1'});
    };

    //上拉加载医生
    $scope.vm = {
      moreData: true,
      pageNo: 1,
      init: function () {
        $scope.spinnerShow = true;
        $scope.introductions = null;
        $scope.vm.pageNo = 1;
        $scope.vm.moreData = true;
        getDoctorIntroductions({searchName: $scope.searchName, pageNo: $scope.vm.pageNo}, true);
      },
      loadMore: function () {
        $scope.vm.pageNo++;
        getDoctorIntroductions({searchName: $scope.searchName, pageNo: $scope.vm.pageNo}, false);
      }
    };

    $scope.$on('$ionicView.beforeEnter', function(){
      var forwardViewId = $ionicHistory.currentView().forwardViewId;
      $scope.searchName = $scope.searchNameTmp;
      $scope.vm.moreData = true;
      if (angular.isUndefined(forwardViewId) || forwardViewId === null || forwardViewId === '') {
        if ((angular.isUndefined($scope.introductions) || $scope.introductions.length === 0) ||
            (!angular.isUndefined($scope.searchNameTmp) && $scope.searchNameTmp !== '')) {
          $scope.httpIndex = {index:1};
          $scope.searchNameTmp = '';
          $scope.searchName = '';
          $scope.vm.init();
        }
      }
    });

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
    $stateProvider.state('doctorIntroductionList', {
      url: '/doctorIntroductionList',
      templateUrl: 'modules/doctorIntroduction/doctorIntroductionList.html',
      controller: doctorIntroductionListCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
