'use strict';

(function(app) {

  //define constants
  app.constant(
    'appConstants',
    {
      'piwikServer' : 'https://server.propersoft.cn:8082',
      'piwikTrackTarget' : 'server.propersoft.cn/isj',
      'paymentServer' : 'http://weixin.propersoft.cn/iosj'
    }

  );

})(angular.module('isj'));
