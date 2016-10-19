'use strict';

(function(app) {
  //暂时去掉照片缓存
  app.factory('doctorPhotoService', function() {
    var factory = {};

    factory.setPhoto = function() {
      //if (!angular.isUndefined(image) && image !== '') {
      //  $window.localStorage['doctorPhoto_'+doctorId] = image;
      //}
    };

    factory.getPhoto = function() {
      //return $window.localStorage['doctorPhoto_'+doctorId];
      return '';
    };

    return factory;
  });
})(angular.module('isj'));
