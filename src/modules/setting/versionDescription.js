(function(app) {
  'use strict';

  var versionDescriptionCtrl = function() {

  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('settingVersionDescription', {
      url: '/setting/versionDescription',
      templateUrl: 'modules/setting/versionDescription.html',
      controller: versionDescriptionCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
