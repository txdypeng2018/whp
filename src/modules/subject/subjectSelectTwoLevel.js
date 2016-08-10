(function(app) {
  'use strict';

  var subjectSelectTwoLevelCtrl = function($scope, $http, $state, $stateParams, $timeout) {
    //取得二级学科
    var getSubjects = function() {
      $http.get('/subjects', {params: {subjectId: $stateParams.subjectId, searchName: $scope.searchName}}).success(function(data) {
        $scope.subjects = data;
      });
    };

    //初期化取得学科
    getSubjects();

    //点击搜索提示事件设置焦点
    $scope.placeholderClk = function() {
      $timeout(function() {
        document.getElementById('subjectSelect_search').focus();
      });
    };
    //搜索学科
    $scope.doSearch = function() {
      getSubjects();
    };

    //一级学科选择事件
    $scope.subjectClk = function(id) {
      if ($stateParams.type === '1') {
        $state.go('registerTodayDoctorList', {subjectId: id});
      }
      else if ($stateParams.type === '2') {
        $state.go('registerDoctorDateSelect', {subjectId: id});
      }
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('subjectSelectTwoLevel', {
      url: '/subject/subjectSelectTwoLevel/:type/:subjectId',
      templateUrl: 'modules/subject/subjectSelect.html',
      controller: subjectSelectTwoLevelCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
