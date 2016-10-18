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
      $http.get('/service/userOpinion', {params: param}).success(function(data) {
        $scope.feedbacks = data;
      }).error(function(data){
        $scope.feedbacks = [];
        toastService.show(data);
      });
    };

    $scope.$on('$ionicView.beforeEnter', function(){
      getFeedbackList($scope.searchStr);
    });

    //查询意见列表事件
    $scope.searchFeedback = function() {
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
