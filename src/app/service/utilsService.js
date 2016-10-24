'use strict';

(function(app) {
  app.factory('utilsService', function() {
    var factory = {};

    factory.getDistrictColor = function(districts) {
      var districtColor = {};
      var color = ['district-icon-positive', 'district-icon-balanced',
        'district-icon-royal', 'district-icon-calm', 'district-icon-assertive'];
      for (var i = 0 ; i < districts.length ; i++) {
        var district = districts[i];
        districtColor[district.id] = color[i];
      }
      return districtColor;
    };

    return factory;
  });
})(angular.module('isj'));
