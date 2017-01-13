(function(app) {
  'use strict';

  var tabMessageCtrl = function($scope, $http, toastService, $ionicHistory) {
    //取得消息列表
    var getMessages = function() {
      $scope.messages = null;
      $http.get('/messages', {params: {index: $scope.httpIndex.index}}).success(function(data, status, headers, config) {
        if (angular.isUndefined($scope.httpIndex[config.params.index])) {
          for (var i = 0 ; i < data.length ; i++) {
            data[i].content = data[i].content.replaceAll('\n','<br/>');
          }
          $scope.messages = data;
        }
      }).error(function(data, status, fun, config){
        if (angular.isUndefined($scope.httpIndex[config.params.index])) {
          $scope.messages = [];
          toastService.show(data);
        }
      }).finally(function() {
        $scope.$broadcast('scroll.refreshComplete');
      });
    };

    $scope.$on('$ionicView.beforeEnter', function(){
      $scope.httpIndex = {index:1};
      getMessages();
      $ionicHistory.clearHistory();
    });

    //遮蔽罩取消
    $scope.spinnerCancel = function() {
      $scope.httpIndex[$scope.httpIndex.index] = 'CANCEL';
      $scope.$broadcast('scroll.refreshComplete');
    };

    //下拉刷新
    $scope.doRefresh = function() {
      $scope.httpIndex.index++;
      getMessages();
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('tab.message', {
      url: '/message',
      views:{
        'tab-message':{
          templateUrl: 'modules/tab/message/message.html',
          controller: tabMessageCtrl
        }
      }
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
