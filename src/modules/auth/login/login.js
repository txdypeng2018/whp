(function(app) {
  'use strict';

  var loginCtrl = function($scope, $http, $document, $location, $window, $log) {
    $scope.login = function() {
      $scope.logging = true;
      $http.post('/auth/login', $scope.user).then(
        function(response) {
          $scope.logging = false;
          $scope.loginErr = false;
          $window.localStorage.token = response.data;
          $location.url('/');
        }, function(response) {
          $log.debug(response);
          $scope.logging = false;
          $scope.loginErr = true;
          $scope.user = {};
          document.getElementById('uname').focus();
          delete $window.localStorage.token;
        }
      );
    };
  };

  var loginRouter = function($stateProvider) {
    $stateProvider.state('login', {
      url: '/auth/login',
      templateUrl: 'modules/auth/login/login.html',
      controller: loginCtrl
    });
  };

  app.config(loginRouter);
})(angular.module('pea'));
