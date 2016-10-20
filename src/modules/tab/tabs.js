(function(app) {
  'use strict';

  app.config(function($ionicConfigProvider) {
    $ionicConfigProvider.platform.ios.tabs.style('standard');
    $ionicConfigProvider.platform.ios.tabs.position('bottom');
    $ionicConfigProvider.platform.android.tabs.style('standard');
    $ionicConfigProvider.platform.android.tabs.position('bottom');
    $ionicConfigProvider.platform.ios.navBar.alignTitle('center');
    $ionicConfigProvider.platform.android.navBar.alignTitle('center');
    $ionicConfigProvider.platform.ios.backButton.previousTitleText('').icon('ion-ios-arrow-thin-left');
    $ionicConfigProvider.platform.android.backButton.previousTitleText('').icon('ion-android-arrow-back');
    $ionicConfigProvider.platform.ios.views.transition('ios');
    $ionicConfigProvider.platform.android.views.transition('android');
    $ionicConfigProvider.form.checkbox('circle');
  });

  var tabCtrl = function($scope, $http, $state, userService) {
    //路由跳转
    $scope.tabRouter = function(routerId) {
      var isLogin = true;
      if (routerId === 'tab.message' || routerId === 'tab.registration') {
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
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('tab', {
      url: '/tab',
      abstract: true,
      templateUrl: 'modules/tab/tabs.html',
      controller: tabCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
