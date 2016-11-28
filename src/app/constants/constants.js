'use strict';

(function(app) {

  //define constants
  app.constant(
    'appConstants',
    {
      'piwikServer' : 'https://server.propersoft.cn:8082',
      'piwikTrackTarget' : 'server.propersoft.cn/ihos',
      'properpush':{
	  		'appId':'isj',
	  		'pushUrl':'https://sjh.sj-hospital.org/properpush',
	  		'secretKey':'b2024e00064bc5d8db70fdee087eae4f',
	  		'xiaomi':{
	  			'theAppid':'2882303761517501901',
	  			'theAppkey':'5591750110901'
	  		}
  		}
    }

  );

})(angular.module('isj'));
