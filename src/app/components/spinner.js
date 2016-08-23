'use strict';

angular.module('isj').directive('isjSpinner', function() {
  return {
    restrict: 'E',
    replace: false,
    scope:{
      isShow: '=isShow',
      isRefresh: '=isRefresh'
    },
    template: '',
    controller:function($scope, $element, ngProgressFactory) {
      $scope.progressbar = ngProgressFactory.createInstance();
      $scope.progressbar.setParent($element[0]);
      $scope.progressbar.setColor('#387ef5');
      $scope.$watch('isRefresh', function (newValue) {
        if ($scope.isShow) {
          if (newValue === 1) {
            $scope.progressbar.start();
          }
          else {
            $scope.progressbar.set(0);
            $scope.progressbar.start();
          }
        }
        else {
          $scope.progressbar.complete();
        }
      });
    }
  };
});
