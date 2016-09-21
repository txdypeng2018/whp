(function(app) {
  'use strict';

  var hospitalIntroduceCtrl = function($scope, $sce) {
    $scope.myURL = $sce.trustAsResourceUrl('http://sj-hospital.org/');
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('hospitalIntroduce', {
      url: '/hospitalIntroduce',
      cache: false,
      templateUrl: 'modules/hospitalIntroduce/hospitalIntroduce.html',
      controller: hospitalIntroduceCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
