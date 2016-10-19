(function(app) {
  'use strict';

  var feedbackListCtrl = function($scope, $http, $state, toastService) {
    //初始化反馈状态查询下拉菜单
    $scope.searchStr = {
      feedbackStatus: ''
    };
    $scope.feedbackStatusTypes = [];
    $scope.feedbackStatusTypes.push({
      code: '',
      name: '全部'
    });
    //取得反馈状态列表
    $http.get('/dataBase/feedbackTypes').success(function(data) {
      for (var i = 0 ; i < data.length ; i++) {
        $scope.feedbackStatusTypes.push(data[i]);
      }
    }).error(function(data){
      toastService.show(data);
    });

    //取得意见列表
    var getFeedbackList = function(param) {
      $scope.feedbacks = null;
      param.index = $scope.httpIndex.index;
      $http.get('/service/userOpinion', {params: param}).success(function(data, status, headers, config) {
        if (angular.isUndefined($scope.httpIndex[config.params.index])) {
          $scope.feedbacks = data;
        }
      }).error(function(data, status, fun, config){
        if (angular.isUndefined($scope.httpIndex[config.params.index])) {
          $scope.feedbacks = [];
          toastService.show(data);
        }
      }).finally(function() {
        $scope.$broadcast('scroll.refreshComplete');
      });
    };

    $scope.$on('$ionicView.beforeEnter', function(){
      $scope.httpIndex = {index:1};
      getFeedbackList($scope.searchStr);
    });

    //查询意见列表事件
    $scope.searchFeedback = function() {
      $scope.httpIndex.index++;
      getFeedbackList($scope.searchStr);
    };

    //添加意见
    $scope.opinionAdd = function() {
      $state.go('settingFeedback');
    };

    //查看意见
    $scope.opinionView = function(id) {
      $state.go('settingFeedbackView', {opinionId: id});
    };

    //遮蔽罩取消
    $scope.spinnerCancel = function() {
      $scope.httpIndex[$scope.httpIndex.index] = 'CANCEL';
      $scope.$broadcast('scroll.refreshComplete');
    };

    //下拉刷新
    $scope.doRefresh = function() {
      $scope.httpIndex.index++;
      getFeedbackList($scope.searchStr);
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('settingFeedbackList', {
      url: '/setting/feedbackList',
      templateUrl: 'modules/setting/feedbackList.html',
      controller: feedbackListCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
