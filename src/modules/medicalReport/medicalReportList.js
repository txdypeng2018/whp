(function(app) {
  'use strict';

  var medicalReportListCtrl = function($scope, $http, $window, $state, $stateParams, $cordovaToast) {
    $scope.memberId = $stateParams.memberId;

    //取得检验报告列表
    var getMedicalReports = function(param) {
      param.memberId = $stateParams.memberId;
      $http.get('/medicalReports', {params: param}).success(function(data) {
        $scope.reports = data;
      }).error(function(data){
        $scope.reports = [];
        $cordovaToast.showShortBottom(data);
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

    $scope.$on('$ionicView.beforeEnter', function(){
      $scope.patient = {};
      $scope.reports = null;
      //取得就诊人
      $http.get('/user/familyMembers/familyMember', {params: {memberId: $stateParams.memberId}}).success(function(data) {
        $scope.patient = data;
      }).error(function(data){
        $cordovaToast.showShortBottom(data);
      });

      getMedicalReports($scope.searchStr);
    });

    //返回上页
    $scope.goBack = function() {
      $state.go('tab.main');
    };

    //选择家庭成员
    $scope.selectMember = function() {
      $state.go('familyMemberSelect', {skipId: 'medicalReportList', memberId: $scope.patient.id});
    };

    //查看检验报告详细
    $scope.viewReport = function(id, category, status) {
      if (status === '1') {
        if (category === '1') {
          $state.go('medicalReportView1', {id: id, category: category});
        }
      }
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('medicalReportList', {
      url: '/medicalReportList/:memberId',
      templateUrl: 'modules/medicalReport/medicalReportList.html',
      controller: medicalReportListCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
