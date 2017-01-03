'use strict';

(function(app) {
  app.factory('toastService', function($cordovaToast, $cordovaDevice) {
    var factory = {};

    factory.show = function(message, duration, position) {
      if (angular.isUndefined(duration) || duration === '') {
        duration = 'long';
      }
      if (angular.isUndefined(position) || position === '') {
        position = 'bottom';
      }
      if (position === 'bottom') {
        var platform = $cordovaDevice.getPlatform();
        var addPixelsY = -40;
        if (platform === 'Android') {
          addPixelsY = -120;
        }
        $cordovaToast.showWithOptions(
          {
            message: message,
            duration: duration,
            position: position,
            addPixelsY: addPixelsY
          }
        );
      }
      else {
        $cordovaToast.show(message, duration, position);
      }
    };

    return factory;
  });
})(angular.module('isj'));
