(function(app) {
  'use strict';

  var tabMessageCtrl = function($scope, $http, $cordovaToast) {
    $scope.$on('$ionicView.beforeEnter', function(){
      //取得消息列表
      $scope.messages = null;
      $http.get('/messages').success(function(data) {
        $scope.messages = data;
      }).error(function(data){
        $scope.messages = [];
        $cordovaToast.showShortBottom(data);
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
