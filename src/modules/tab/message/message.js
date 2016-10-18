(function(app) {
  'use strict';

  var tabMessageCtrl = function($scope, $http, toastService) {
    $scope.$on('$ionicView.beforeEnter', function(){
      //取得消息列表
      $scope.messages = null;
      $http.get('/messages').success(function(data) {
        for (var i = 0 ; i < data.length ; i++) {
          data[i].content = data[i].content.replaceAll('\n','<br/>');
        }
        $scope.messages = data;
      }).error(function(data){
        $scope.messages = [];
        toastService.show(data);
      });
    });
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
