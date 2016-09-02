(function(app) {
  'use strict';

  var loginCtrl = function($scope, $http, $state, $stateParams, $window, $ionicHistory, $cordovaToast, userService) {
    $scope.$on('$ionicView.beforeEnter', function(){
      $scope.input = {
        phone: '',
        password: ''
      };
    });

    //返回
    $scope.goBack = function() {
      if (angular.isUndefined($stateParams.skipId) || $stateParams.skipId === '') {
        $ionicHistory.goBack();
      }
      else {
        $state.go($stateParams.skipId);
      }
    };

    //登录
    $scope.login = function() {
      var phoneAndPwd = {
        phone: $scope.input.phone.toString(),
        password: $scope.input.password
      };
      $http.post('/permission/login', phoneAndPwd).success(function(data) {
        userService.setToken(data);
        if (angular.isUndefined($stateParams.skipId) || $stateParams.skipId === '') {
          $ionicHistory.goBack();
        }
        else {
          $state.go($stateParams.skipId);
        }
      }).error(function(data){
        $cordovaToast.showShortBottom(data);
      });
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('login', {
      url: '/login/:skipId',
      templateUrl: 'modules/login/login.html',
      controller: loginCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));

