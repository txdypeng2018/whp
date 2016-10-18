'use strict';

(function(app) {
  app.factory('toastService', function($cordovaToast) {
    var factory = {};

    factory.show = function(message, duration, position) {
      if (angular.isUndefined(duration) || duration === '') {
        duration = 'long';
      }
      if (angular.isUndefined(position) || position === '') {
        position = 'bottom';
      }
      $cordovaToast.show(message, duration, position);
    };

    return factory;
  });
})(angular.module('isj'));
