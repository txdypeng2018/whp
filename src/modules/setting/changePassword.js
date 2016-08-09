(function(app) {
  'use strict';

  var changePasswordCtrl = function() {

  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('settingChangePassword', {
      url: '/setting/changePassword',
      templateUrl: 'modules/setting/changePassword.html',
      controller: changePasswordCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
