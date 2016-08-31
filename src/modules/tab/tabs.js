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
  });

  var tabCtrl = function($scope, $http, $state, $cordovaToast, userService) {
    //路由跳转
    $scope.tabRouter = function(routerId) {
      var isLogin = true;
      if (routerId === 'tab.message' || routerId === 'tab.registration') {
        isLogin = userService.hasToken();
      }
      if (isLogin) {
        $state.go(routerId);
      }
      else {
        $state.go('login');
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
