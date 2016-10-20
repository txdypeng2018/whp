(function(app) {
  'use strict';

  var tabPersonalCtrl = function($scope, $http, $state, toastService, userService, $ionicHistory) {
    $scope.$on('$ionicView.beforeEnter', function(){
      //取得用户信息
      $scope.isLogin = userService.hasToken();
      $http.get('/user/userInfo').success(function(data) {
        $scope.isLogin = true;
        $scope.user = data;
        if ($scope.user.phone !== null && $scope.user.phone !== '') {
          $scope.user.phone = $scope.user.phone.substring(0,3)+'****'+$scope.user.phone.substring(7,11);
        }
      }).error(function(data, status){
        if (status !== 401) {
          toastService.show(data);
        }
        else {
          $scope.user = {};
          userService.clearToken();
          $scope.isLogin = false;
        }
      });

      //取得家庭成员数量
      $http.get('/user/familyMembers/count').success(function(data) {
        $scope.memberCount = data;
      }).error(function(data){
        if (status !== 401) {
          toastService.show(data);
        }
        else {
          $scope.memberCount = '';
        }
      });

      //取得收藏医生数量
      $http.get('/user/collectionDoctors/count').success(function(data) {
        $scope.doctorCount = data;
      }).error(function(data){
        if (status !== 401) {
          toastService.show(data);
        }
        else {
          $scope.doctorCount = '';
        }
      });

      $ionicHistory.clearHistory();
    });

    //路由跳转
    $scope.itemRouter = function(routerId) {
      var isLogin = true;
      if (routerId !== 'settingIndex' && routerId !== 'settingAppAbout') {
        isLogin = userService.hasToken();
        if (isLogin) {
          $http.get('/user/tokenVal').success(function() {
            $state.go(routerId);
          }).error(function(data, status){
            if (status !== 401) {
              $state.go(routerId);
            }
            else {
              userService.clearToken();
              $state.go('login');
            }
          });
        }
        else {
          $state.go('login');
        }
      }
      else {
        $state.go(routerId);
      }
    };

    //登录点击事件
    $scope.login = function() {
      if (!$scope.isLogin) {
        $state.go('login');
      }
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('tab.personal', {
      url: '/personal',
      views:{
        'tab-personal':{
          templateUrl: 'modules/tab/personal/personal.html',
          controller: tabPersonalCtrl
        }
      }
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
