(function(app) {
  'use strict';

  var collectionDoctorListCtrl = function($scope, $http, $state, $timeout, $cordovaToast) {
    $scope.title = '收藏的医生';
    $scope.vm = {
      moreData: false,
      loadMore: function () {}
    };

    //取得医生照片
    var getDoctorPhoto = function(doctorId, index) {
      $http.get('/doctors/photo', {params: {doctorId: doctorId, index: index}}).success(function(data, status, headers, config) {
        $scope.introductions[config.params.index].photo = data;
      }).error(function(data, status, fun, config){
        if (status === 404) {
          $scope.introductions[config.params.index].photo = '';
        }
        else {
          $cordovaToast.showShortBottom(data);
        }
      });
    };

    //取得收藏医生介绍列表
    var getDoctorIntroductions = function(param) {
      $http.get('/user/collectionDoctors', {params: param}).success(function(data) {
        $scope.introductions = data;
        for (var i = 0 ; i < data.length ; i++) {
          getDoctorPhoto(data[i].id,i);
        }
      }).error(function(data){
        $cordovaToast.showShortBottom(data);
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
      getDoctorIntroductions({searchName: $scope.searchName});
    };
    //初始化取得医师介绍列表
    $scope.$on('$ionicView.beforeEnter', function(){
      getDoctorIntroductions({searchName: $scope.searchName});
    });

    //查看医生介绍详细
    $scope.viewIntroduction = function(id) {
      $state.go('doctorIntroductionView', {doctorId: id, type: '1'});
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
