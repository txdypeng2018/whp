(function(app) {
  'use strict';

  var deptListCtrl = function() {

  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('mainDeptList', {
      url: '/main/deptList/:type',
      templateUrl: 'modules/main/registration/deptList.html',
      controller: deptListCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
