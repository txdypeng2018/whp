(function(app) {
  'use strict';

  var subjectSelectCtrl = function($scope, $http, $state, $stateParams, $timeout) {
    //取得学科列表
    var getSubjects = function() {
      $http.get('/subjects', {params: {districtId: $scope.districtId}}).success(function(data) {
        $scope.subjects = data;

        //默认选中第一个一级学科
        $timeout(function(){
          angular.element(document.querySelectorAll('.subject-div-left')[0]).addClass('left-activated');
          if (angular.isUndefined(data[0].subjects) || data[0].subjects.length === 0) {
            $scope.subjectRights = [data[0]];
          }
          else {
            $scope.subjectRights = data[0].subjects;
          }
        });
      });
    };

    //取得院区信息
    $http.get('/organization/district').success(function(data) {
      $scope.districts = data;
      getSubjects();
    });

    //查询框显示隐藏事件
    $scope.searchClk = function() {
      if (angular.element(document.querySelector('.head-search')).hasClass('search-none')) {
        angular.element(document.querySelector('.head-search')).removeClass('search-none');
        $timeout(function(){
          document.getElementById('subjectSelect_search').focus();
        });
      }
      else {
        angular.element(document.querySelector('.head-search')).addClass('search-none');
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
    $scope.districtClk = function(event, id) {
      $scope.districtId = id;
      var districtDocuments = document.querySelectorAll('.district-button');
      if (!angular.element(event.currentTarget).hasClass('button-outline')) {
        angular.element(event.currentTarget).addClass('button-outline');
        $scope.districtId = '';
      }
      else {
        for (var i = 0 ; i < districtDocuments.length ; i++) {
          if (!angular.element(districtDocuments[i]).hasClass('button-outline')) {
            angular.element(districtDocuments[i]).addClass('button-outline');
          }
        }
        angular.element(event.currentTarget).removeClass('button-outline');
      }

      getSubjects();
    };

    //左侧一级学科选择事件
    $scope.subjectLeftClk = function(event, id) {
      angular.element(document.querySelectorAll('.subject-div-left')).removeClass('left-activated');
      angular.element(event.currentTarget).addClass('left-activated');
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
    };

    //右侧一级科室选择事件
    $scope.subjectRightClk = function(id) {
      $timeout(function(){
        if ($stateParams.type === '1') {
          $state.go('registerTodayDoctorList', {districtId: $scope.districtId, subjectId: id});
        }
        else if ($stateParams.type === '2') {
          $state.go('registerDoctorDateSelect', {districtId: $scope.districtId, subjectId: id});
        }
      }, 10);
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('subjectSelect', {
      url: '/subject/subjectSelect/:type',
      cache: 'false',
      templateUrl: 'modules/subject/subjectSelect.html',
      controller: subjectSelectCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
