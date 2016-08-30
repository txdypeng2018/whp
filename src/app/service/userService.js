'use strict';

(function(app) {
  app.factory('userService', function($window) {
    var factory = {};

    factory.setToken = function(token) {
      $window.localStorage.token = token;
    };

    factory.getToken = function() {
      return $window.localStorage.token;
    };

    factory.hasToken = function() {
      return (!angular.isUndefined($window.localStorage.token) && $window.localStorage.token !== '');
    };

    factory.clearToken = function() {
      $window.localStorage.token = '';
    };

    return factory;
  });
})(angular.module('isj'));
