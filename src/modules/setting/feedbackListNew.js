/**
 * Created by Administrator on 2017/1/6.
 */
(function(app) {
  'use strict';

  var feedbackListCtrl = function($scope, $http, $state, toastService, $ionicScrollDelegate) {
    //取得意见列表
    var getFeedbackList = function() {
      $scope.feedbacks = [];
      var param = {};
      param.index = $scope.httpIndex.index;
      $http.get('/service/userOpinion', {params: param}).success(function(data, status, headers, config) {
        if (angular.isUndefined($scope.httpIndex[config.params.index])) {
          for(var i = 0; i < data.length; i++){
            $scope.feedbacks.push(data[data.length - 1 - i]);
          }
        }
        $ionicScrollDelegate.scrollBottom();
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
      getFeedbackList();
      isSubmitDisable();
      $ionicScrollDelegate.scrollBottom();
    });

    //判断提交按钮是否disabled
    $scope.inputOpinion = undefined;
    var isSubmitDisable = function(){
      $scope.$watch('inputOpinion',function(newValue){
        $scope.inputOpinion = newValue;
        if(angular.isUndefined($scope.inputOpinion) || $scope.inputOpinion === ''){
          $scope.isSubmit=false;
        }else{
          $scope.isSubmit=true;
        }
      });
    };

    //提交意见
    $scope.submitOpinion = function(){
      $http.post('/service/userOpinion', {opinion: $scope.inputOpinion}).success(function(){
        $scope.doRefresh();
        $ionicScrollDelegate.scrollBottom();
        $scope.inputOpinion = '';
      }).error(function(data){
        $scope.isSubmit = false;
        toastService.show(data);
      });
    };

    //刷新
    $scope.doRefresh = function() {
      $scope.httpIndex.index++;
      getFeedbackList();
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('settingFeedbackListNew', {
      url: '/setting/feedbackListNew',
      templateUrl: 'modules/setting/feedbackListNew.html',
      controller: feedbackListCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
