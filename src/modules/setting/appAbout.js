(function(app) {
  'use strict';

  var appAboutCtrl = function($scope, $http, $state, userService) {
    cordova.getAppVersion.getVersionNumber(function (versionNumber) {
      $scope.versionNumber = versionNumber;
    });

    cordova.getAppVersion.getVersionCode(function (versionCode) {
      $scope.versionCode = versionCode;
    });

    $scope.$on('$ionicView.beforeEnter', function(){
      $scope.isLogin = userService.hasToken();
    });

    //取得客服电话
    $http.get('/service/phone').success(function(data) {
      $scope.servicePhone = data;
    });

    //路由跳转
    $scope.itemRouter = function(routerId) {
      $state.go(routerId);
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('settingAppAbout', {
      url: '/setting/appAbout',
      templateUrl: 'modules/setting/appAbout.html',
      controller: appAboutCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
