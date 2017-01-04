(function(app) {
  'use strict';

  var appAboutCtrl = function($scope, $http, $state, userService) {
    cordova.getAppVersion.getVersionNumber(function (versionNumber) {
      $scope.versionNumber = versionNumber;
    });

    cordova.getAppVersion.getVersionCode(function (versionCode) {
      $scope.versionCode = versionCode;
      $http.get('/app/versions/' + versionCode).success(function(data) {
        $scope.description = data.note;
      });
    });

    //取得客服电话
    var getPhone = function() {
      $http.get('/service/phone').success(function(data) {
        $scope.servicePhone = data;
      });
    };

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

      if (angular.isUndefined($scope.servicePhone) || $scope.servicePhone === '') {
        getPhone();
      }
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
