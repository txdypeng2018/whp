(function(app) {
  'use strict';

  var medicalReportCtrl = function($scope, $http, $window) {
    var getMedicalReports = function(param) {
      $http.get('/main/medicalReport', {params: param}).success(function(data) {
        $scope.reports = data;
      }).finally(function() {
        $scope.$broadcast('scroll.refreshComplete');
      });
    };

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
    $scope.searchChange = function() {
      getMedicalReports($scope.searchStr);
    };
    $scope.reportRefresh = function() {
      getMedicalReports($scope.searchStr);
    };
    getMedicalReports($scope.searchStr);

    $scope.$watch('$viewContentLoaded', function() {
      document.getElementById('medicalReport_reports').style.height =
        (document.getElementById('medicalReport_Content').offsetHeight - 98) + 'px';
    });
    angular.element($window).bind('resize', function() {
      document.getElementById('medicalReport_reports').style.height =
        (document.getElementById('medicalReport_Content').offsetHeight - 98) + 'px';
    });
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('mainMedicalReport', {
      url: '/main/medicalReport',
      cache: 'false',
      templateUrl: 'modules/main/medicalReport/medicalReport.html',
      controller: medicalReportCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
