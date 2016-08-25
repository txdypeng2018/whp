'use strict';

angular.module('isj').directive('isjBackButton', function() {
  return {
    restrict: 'E',
    replace: true,
    scope: {
      goBack: '&?'
    },
    template: '<a class="icon-return positive" ng-click="goBack()"><i class="icon ion-ios-arrow-left"></i> <div>返回</div></a>',
    controller:function($scope, $ionicHistory) {
      if (angular.isUndefined($scope.goBack)) {
        $scope.goBack = function() {
          $ionicHistory.goBack();
        };
      }
    }
  };
});
