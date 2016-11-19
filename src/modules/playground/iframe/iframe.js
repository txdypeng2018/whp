'use strict';

(function(app) {

  var ctrl = function($scope, $location, $sce) {
    console.debug('$scope of iframe: %o', $scope);
    console.debug('url: %s', $location.search().url);
    $scope.iframeSrc = $sce.trustAsResourceUrl($location.search().url);
  };

  var router = function($stateProvider) {
    $stateProvider.state('main.playgroundIframe', {
      url: '^/playground/iframe?url',
      controller: ctrl,
      templateUrl: 'modules/playground/iframe/iframe.html'
    });
  };

  app.config(router);

})(angular.module('pea'));
