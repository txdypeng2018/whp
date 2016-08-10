(function(app) {
  'use strict';

  var subjectSelectOneLevelCtrl = function($scope, $http, $state, $stateParams, $timeout) {
    //取得一级学科
    var getSubjects = function() {
      $http.get('/subjects', {params: {subjectId: '', searchName: $scope.searchName}}).success(function(data) {
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
    $scope.subjectClk = function(id, hasNext) {
      if (hasNext === '1') {
        $state.go('subjectSelectTwoLevel', {type: $stateParams.type, subjectId: id});
      }
      else {
        if ($stateParams.type === '1') {
          $state.go('registerTodayDoctorList', {subjectId: id});
        }
        else if ($stateParams.type === '2') {
          $state.go('registerDoctorDateSelect', {subjectId: id});
        }
      }
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('subjectSelectOneLevel', {
      url: '/subject/subjectSelectOneLevel/:type',
      templateUrl: 'modules/subject/subjectSelect.html',
      controller: subjectSelectOneLevelCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
