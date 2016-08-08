(function(app) {
  'use strict';

  var tabMessageCtrl = function($scope, $http) {
    //取得消息列表
    $http.get('/messages').success(function(data) {
      $scope.messages = data;
    });
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('tab.message', {
      url: '/message',
      cache: 'false',
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
