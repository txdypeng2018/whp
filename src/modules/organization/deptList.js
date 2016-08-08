(function(app) {
  'use strict';

  var organizationDeptListCtrl = function($scope, $http, $window, $state, $stateParams, $timeout) {
    var type = $stateParams.type;

    //取得科室列表
    var getDept = function(param) {
      $http.get('/organization/depts', {params: param}).success(function(data) {
        $scope.depts = data;

        //默认选中第一个一级科室
        $timeout(function(){
          angular.element(document.querySelectorAll('.dept-div-left')[0]).addClass('positive');
          angular.element(document.querySelectorAll('.dept-div-left')[0]).addClass('left-activated');
          $scope.deptRights = data[0].depts;
        });
      });
    };

    //院区选择事件
    $scope.districtClk = function(event, id) {
      var districtDocuments = document.querySelectorAll('.district-button');
      for (var i = 0 ; i < districtDocuments.length ; i++) {
        if (!angular.element(districtDocuments[i]).hasClass('button-outline')) {
          angular.element(districtDocuments[i]).addClass('button-outline');
        }
      }
      angular.element(event.currentTarget).removeClass('button-outline');

      getDept({'id': id});
    };

    //左侧一级科室选择事件
    $scope.deptLeftClk = function(event, id) {
      angular.element(document.querySelectorAll('.dept-div-left')).removeClass('positive');
      angular.element(document.querySelectorAll('.dept-div-left')).removeClass('left-activated');
      angular.element(event.currentTarget).addClass('positive');
      angular.element(event.currentTarget).addClass('left-activated');
      for (var i = 0 ; i < $scope.depts.length ; i++) {
        if ($scope.depts[i].id === id) {
          $scope.deptRights = $scope.depts[i].depts;
          break;
        }
      }
    };

    //右侧一级科室选择事件
    $scope.deptRightClk = function(id) {
      $timeout(function(){
        if (type === '1') {
          $state.go('registerTodayDoctorList', {id: id});
        }
        else if (type === '2') {
          $state.go('registerDoctorDateSelect', {id: id});
        }
      }, 10);
    };

    //取得院区信息
    $http.get('/organization/district').success(function(data) {
      $scope.districts = data;

      //默认选中第一个院区
      $timeout(function(){
        if ($scope.districts.length > 0) {
          angular.element(document.querySelectorAll('.district-button')[0]).removeClass('button-outline');
          getDept({'id': $scope.districts[0].id});
        }
      });
    });
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('organizationDeptList', {
      url: '/organization/deptList/:type',
      cache: 'false',
      templateUrl: 'modules/organization/deptList.html',
      controller: organizationDeptListCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
