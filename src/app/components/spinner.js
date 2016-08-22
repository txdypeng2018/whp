'use strict';

angular.module('isj').directive('isjSpinner', function() {
  return {
    restrict: 'E',
    replace: false,
    scope:{
      isShow: '=isShow'
    },
    template: '',
    controller:function($scope, $element, ngProgressFactory) {
      $scope.progressbar = ngProgressFactory.createInstance();
      $scope.progressbar.setParent($element[0]);
      $scope.progressbar.setColor('#387ef5');
      $scope.$watch('isShow', function (newValue, oldValue) {
        if (newValue) {
          $scope.progressbar.start();
        }
        else if (!newValue && oldValue) {
          $scope.progressbar.complete();
        }
      });
    }
  };
});
