'use strict';

angular.module('isj').directive('isjBackButton', function() {
  return {
    restrict: 'E',
    replace: true,
    scope: {
      goBack: '&?',
      callback: '&'
    },
    template: '<a class="icon-return positive" ng-click="goBack()"><i class="icon ion-ios-arrow-left"></i> <div>返回</div></a>',
    controller:function($scope, $rootScope, $ionicHistory) {
      if (angular.isUndefined($scope.goBack)) {
        $scope.goBack = function() {
          $rootScope.requestIndex = 0;
          $rootScope.inProcess = false;
          $ionicHistory.goBack();
          $scope.callback();
        };
      }
    }
  };
});
