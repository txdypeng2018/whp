(function(app) {
  'use strict';

  var medicalReportListCtrl = function($scope, $http, $window, $state) {
    //取得检验报告列表
    var getMedicalReports = function(param) {
      $http.get('/medicalReports', {params: param}).success(function(data) {
        $scope.reports = data;
      }).finally(function() {
        $scope.$broadcast('scroll.refreshComplete');
      });
    };

    //初始化检验报告查询条件
    $scope.searchStr = {
      searchTime: '',
      searchStatus: ''
    };
    $scope.searchTimeTypes = [
      {code:'', name :'时间不限'},
      {code:'week', name :'一周内'},
      {code:'month', name :'一月内'},
      {code:'halfYear', name :'半年内'},
      {code:'year', name :'一年内'}
    ];
    $scope.searchStatusTypes = [
      {code:'', name :'状态不限'},
      {code:'1', name :'已出'},
      {code:'0', name :'未出'}
    ];

    //查询检验报告列表事件
    $scope.searchReport = function() {
      getMedicalReports($scope.searchStr);
    };
    //初始化取得检验报告列表
    getMedicalReports($scope.searchStr);

    //查看检验报告详细
    $scope.viewReport = function(id, category, status) {
      if (status === '1') {
        if (category === '1') {
          $state.go('medicalReportView1', {id: id, category: category});
        }
      }
    };

    //设置报告列表高度
    $scope.$watch('$viewContentLoaded', function() {
      document.getElementById('medicalReportList_reports').style.height =
        (document.getElementById('medicalReportList_content').offsetHeight - 98) + 'px';
    });
    angular.element($window).bind('resize', function() {
      document.getElementById('medicalReportList_reports').style.height =
        (document.getElementById('medicalReportList_content').offsetHeight - 98) + 'px';
    });
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('medicalReportList', {
      url: '/medicalReportList',
      cache: 'false',
      templateUrl: 'modules/medicalReport/medicalReportList.html',
      controller: medicalReportListCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
