(function(app) {
  'use strict';

  var settingIndexCtrl = function($scope, $state, $http, $ionicPopup, userService) {
    $scope.$on('$ionicView.beforeEnter', function(){
      $scope.isLogin = userService.hasToken();
      if ($scope.isLogin) {
        $http.get('/user/tokenVal').error(function(data, status){
          if (status === 401) {
            userService.clearToken();
            $scope.isLogin = false;
          }
        });
      }
    });
    $scope.$on('$ionicView.beforeLeave', function(){
      if (confirmPopup !== null) {
        confirmPopup.close();
      }
    });

    //路由跳转
    $scope.itemRouter = function(routerId) {
      $state.go(routerId);
    };

    //退出登录
    var confirmPopup = null;
    $scope.quit = function(){
      confirmPopup = $ionicPopup.confirm({
        title: '提示',
        template: '您确定要退出当前账户?',
        cssClass: 'confirm-popup',
        cancelText: '取消',
        okText: '确定'
      });
      confirmPopup.then(function(res) {
        if(res) {
          userService.clearToken();
          $state.go('login', {skipId: 'tab.personal'});
        }
      });
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('settingIndex', {
      url: '/setting/settingIndex',
      templateUrl: 'modules/setting/settingIndex.html',
      controller: settingIndexCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
