'use strict';

(function(app) {
  app.factory('doctorPhotoService', function($window) {
    var factory = {};

    factory.setPhoto = function(doctorId, image) {
      if (!angular.isUndefined(image) && image !== '') {
        $window.localStorage['doctorPhoto_'+doctorId] = image;
      }
    };

    factory.getPhoto = function(doctorId) {
      return $window.localStorage['doctorPhoto_'+doctorId];
    };

    return factory;
  });
})(angular.module('isj'));
