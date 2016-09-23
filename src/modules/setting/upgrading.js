(function(app) {
  'use strict';

  var upgradingCtrl = function($scope, $stateParams) {
    $scope.title = $stateParams.title;
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('upgrading', {
      url: '/setting/upgrading/:title',
      templateUrl: 'modules/setting/upgrading.html',
      controller: upgradingCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
