(function(app) {
  'use strict';

  var doctorIntroductionListCtrl = function($scope, $http, $state, $timeout) {
    $scope.title = '医生介绍';

    //取得医生介绍列表
    var getDoctorIntroductions = function(param) {
      $http.get('/doctors', {params: param}).success(function(data) {
        $scope.introductions = data;
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
    getDoctorIntroductions({searchName: $scope.searchName});

    //查看医生介绍详细
    $scope.viewIntroduction = function(id) {
      $state.go('doctorIntroductionView', {doctorId: id});
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
