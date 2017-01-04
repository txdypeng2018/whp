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

    factory.specialCodeReplace = function(specialCode){
      var regSpecialCode = /[^\u4e00-\u9fbf\u002E\u00B7\d\w]/g;
      specialCode = specialCode.replace(regSpecialCode, '');
      return specialCode;
    };

    return factory;
  });
})(angular.module('isj'));
