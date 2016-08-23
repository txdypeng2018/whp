(function(app) {
  'use strict';

  var subjectSelectCtrl = function($scope, $http, $state, $stateParams, $timeout, $ionicHistory) {
    $scope.hideSearch = true;
    $scope.type = $stateParams.type;
    $scope.districtId = '';
    $scope.subjectId = '';

    //取得学科列表
    var getSubjects = function() {
      $http.get('/subjects', {params: {districtId: $scope.districtId}}).success(function(data) {
        $scope.subjects = data;

        //默认选中第一个一级学科
        $scope.subjectId = data[0].id;
        if (angular.isUndefined(data[0].subjects) || data[0].subjects.length === 0) {
          $scope.subjectRights = [data[0]];
        }
        else {
          $scope.subjectRights = data[0].subjects;
        }
      });
    };

    //取得院区信息
    $http.get('/organization/districts').success(function(data) {
      $scope.districts = [{id:'', name:'全部'}];
      for (var i = 0 ; i < data.length ; i++) {
        data[i].name = data[i].name + '院区';
        $scope.districts.push(data[i]);
      }

      getSubjects();
    });

    //返回上页
    $scope.goBack = function() {
      $ionicHistory.goBack();
    };

    //查询框显示隐藏事件
    $scope.searchClk = function() {
      $scope.hideSearch = !$scope.hideSearch;
      if (!$scope.hideSearch) {
        $timeout(function(){
          document.getElementById('subjectSelect_search_'+$scope.type).focus();
        }, 50);
      }
    };

    //查询事件
    $scope.doSearch = function() {
      if (!angular.isUndefined($scope.major) && $scope.major !== '') {
        if ($stateParams.type === '1') {
          $state.go('registerTodayDoctorList', {districtId: $scope.districtId, major: $scope.major});
        }
        else if ($stateParams.type === '2') {
          $state.go('registerDoctorDateSelect', {districtId: $scope.districtId, major: $scope.major});
        }
      }
    };

    //院区选择事件
    $scope.districtClk = function(id) {
      if ($scope.districtId !== id) {
        $scope.districtId = id;
        getSubjects();
      }
    };

    //左侧一级学科选择事件
    $scope.subjectLeftClk = function(id) {
      if ($scope.subjectId !== id) {
        $scope.subjectId = id;

        for (var i = 0 ; i < $scope.subjects.length ; i++) {
          if ($scope.subjects[i].id === id) {
            if (angular.isUndefined($scope.subjects[i].subjects) || $scope.subjects[i].subjects.length === 0) {
              $scope.subjectRights = [$scope.subjects[i]];
            }
            else {
              $scope.subjectRights = $scope.subjects[i].subjects;
            }
            break;
          }
        }
      }
    };

    //右侧一级科室选择事件
    $scope.subjectRightClk = function(id) {
      if ($stateParams.type === '1') {
        $state.go('registerTodayDoctorList', {districtId: $scope.districtId, subjectId: id});
      }
      else if ($stateParams.type === '2') {
        $state.go('registerDoctorDateSelect', {districtId: $scope.districtId, subjectId: id});
      }
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('subjectSelect', {
      url: '/subject/subjectSelect/:type',
      templateUrl: 'modules/subject/subjectSelect.html',
      controller: subjectSelectCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
