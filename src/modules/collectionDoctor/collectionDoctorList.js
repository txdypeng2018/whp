(function(app) {
  'use strict';

  var collectionDoctorListCtrl = function($scope, $http, $state, $timeout, doctorPhotoService, toastService) {
    $scope.title = '收藏的医生';
    $scope.vm = {
      moreData: false,
      loadMore: function () {}
    };

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

    //取得收藏医生介绍列表
    var getDoctorIntroductions = function(param) {
      $scope.introductions = null;
      param.index = $scope.httpIndex.index;
      $http.get('/user/collectionDoctors', {params: param}).success(function(data, status, headers, config) {
        if (angular.isUndefined($scope.httpIndex[config.params.index])) {
          $scope.spinnerShow = false;
          $scope.introductions = data;
          for (var i = 0 ; i < data.length ; i++) {
            getDoctorPhoto(data[i].id,i);
          }
        }
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

    //点击搜索提示事件设置焦点
    $scope.placeholderClk = function() {
      $timeout(function() {
        document.getElementById('doctorIntroduction_search').focus();
      });
    };
    //搜索医生事件
    $scope.doSearch = function() {
      $scope.spinnerShow = true;
      $scope.searchNameTmp = $scope.searchName;
      $scope.httpIndex.index++;
      getDoctorIntroductions({searchName: $scope.searchName});
    };
    //初始化取得医师介绍列表
    $scope.$on('$ionicView.beforeEnter', function(){
      $scope.spinnerShow = true;
      $scope.searchName = $scope.searchNameTmp;
      $scope.httpIndex = {index:1};
      getDoctorIntroductions({searchName: $scope.searchName});
    });

    //查看医生介绍详细
    $scope.viewIntroduction = function(id) {
      $state.go('doctorIntroductionView', {doctorId: id, type: '1'});
    };

    //遮蔽罩取消
    $scope.spinnerCancel = function() {
      $scope.httpIndex[$scope.httpIndex.index] = 'CANCEL';
      $scope.$broadcast('scroll.refreshComplete');
    };

    //返回事件
    $scope.alreadyBack = function() {
      $scope.searchName = '';
      $scope.searchNameTmp = '';
    };

    //下拉刷新
    $scope.doRefresh = function() {
      $scope.spinnerShow = true;
      $scope.httpIndex.index++;
      getDoctorIntroductions({searchName: $scope.searchName});
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('collectionDoctorList', {
      url: '/collectionDoctor/collectionDoctorList',
      templateUrl: 'modules/doctorIntroduction/doctorIntroductionList.html',
      controller: collectionDoctorListCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
