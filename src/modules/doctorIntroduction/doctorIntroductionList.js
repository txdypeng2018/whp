(function(app) {
  'use strict';

  var doctorIntroductionListCtrl = function($scope, $http, $state, $timeout, $cordovaToast, doctorPhotoService) {
    $scope.title = '医生介绍';

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
      $http.get('/doctors', {params: param}).success(function(data) {
        $scope.spinnerShow = false;
        if (data.length === 0) {
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
        $scope.$broadcast('scroll.infiniteScrollComplete');
      }).error(function(data){
        $scope.spinnerShow = false;
        $scope.introductions = [];
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
      $scope.vm.init();
    };

    //查看医生介绍详细
    $scope.viewIntroduction = function(id) {
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
        getDoctorIntroductions({searchName: $scope.searchName, pageNo: $scope.vm.pageNo}, true);
      },
      loadMore: function () {
        $scope.vm.pageNo++;
        getDoctorIntroductions({searchName: $scope.searchName, pageNo: $scope.vm.pageNo}, false);
      }
    };
    $scope.vm.init();
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
