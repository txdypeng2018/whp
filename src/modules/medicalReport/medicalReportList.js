(function(app) {
  'use strict';

  var medicalReportListCtrl = function($scope, $rootScope, $http, $window, $state, $stateParams, $ionicPopup, toastService) {
    $scope.memberId = $stateParams.memberId;

    //温馨提示
    $http.get('/medicalReports/prompt').success(function(data) {
      if (angular.isUndefined($rootScope.reportPrompt) || $rootScope.reportPrompt === '' || $rootScope.reportPrompt !== data) {
        $rootScope.reportPrompt = data;
        showAgreement();
      }
    }).error(function(data){
      toastService.show(data);
    });
    var myPopup = null;
    var showAgreement = function() {
      myPopup = $ionicPopup.show({
        template: '<div style="padding: 3px;font-size:15px">'+$rootScope.reportPrompt+'</div>',
        title: '温馨提示',
        cssClass: 'agreement-popup',
        buttons: [
          {
            text: '我知道了',
            type: 'button-positive',
            onTap: function(e) {
              e.preventDefault();
              myPopup.close();
            }
          }
        ]
      });
    };
    $scope.promptClk = function() {
      showAgreement();
    };
    $scope.$on('$ionicView.beforeLeave', function(){
      if (myPopup !== null) {
        myPopup.close();
      }
    });

    //报告类别切换
    $scope.categoryClk = function(category) {
      $scope.category = category;
      $scope.httpIndex.index++;
      getMedicalReports($scope.searchStr);
    };

    //取得检验报告列表
    var getMedicalReports = function(param) {
      $scope.reports = null;
      param.memberId = $stateParams.memberId;
      param.category = $scope.category;
      param.index = $scope.httpIndex.index;
      $http.get('/medicalReports', {params: param}).success(function(data, status, headers, config) {
        if (angular.isUndefined($scope.httpIndex[config.params.index])) {
          $scope.reports = data;
        }
      }).error(function(data, status, fun, config){
        if (angular.isUndefined($scope.httpIndex[config.params.index])) {
          $scope.reports = [];
          toastService.show(data);
        }
      });
    };

    //初始化检验报告查询条件
    $scope.category = '1';
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
      $scope.httpIndex.index++;
      getMedicalReports($scope.searchStr);
    };

    $scope.$on('$ionicView.beforeEnter', function(){
      $scope.httpIndex = {index:1};
      $scope.patient = {};
      //取得就诊人
      $http.get('/user/familyMembers/familyMember', {params: {memberId: $stateParams.memberId}}).success(function(data) {
        $scope.patient = data;
      }).error(function(data){
        toastService.show(data);
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
    $scope.viewReport = function(id, status) {
      if (status === '1') {
        if ($scope.category === '1') {
          $state.go('medicalReportView1', {id: id, category: $scope.category});
        }
        else {
          $state.go('medicalReportView2', {id: id, category: $scope.category});
        }
      }
      else {
        toastService.show('报告未出，请稍后查看');
      }
    };

    //遮蔽罩取消
    $scope.spinnerCancel = function() {
      $scope.httpIndex[$scope.httpIndex.index] = 'CANCEL';
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
