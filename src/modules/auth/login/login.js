(function(app) {
  'use strict';

  var loginErr = function($scope, $window) {
    $scope.logging = false;
    $scope.loginErr = true;
    $scope.user = {};
    document.getElementById('uname').focus();
    delete $window.localStorage.token;
  };

  var loginCtrl = function($scope, $http, $location, $window, $log) {
    $scope.login = function() {
      $scope.logging = true;
      $http.post('/auth/login', $scope.user).then(
        function(response) {
          var token = response.data;
          var payload = atob(token.split('.')[1]);
          if (JSON.parse(payload).hasRole) {
            $scope.logging = false;
            $scope.loginErr = false;
            $window.localStorage.token = token;
            $location.url('/');
          } else {
            $log.debug('User has no role!');
            loginErr($scope, $window);
          }
        }, function(response) {
          $log.debug(response);
          loginErr($scope, $window);
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
