'use strict';

var template = '<div class="isj-spinner" ng-show="isShow" ng-click="hideSpinner()">';
template += '<div class="loading-div"><ion-spinner icon="ios" class="spinner-dark"></ion-spinner><span>请稍后...</span></div>';
template += '</div>';

angular.module('isj').directive('isjSpinner', function() {
  return {
    restrict: 'E',
    replace: false,
    scope:{
      isShow: '=isShow'
    },
    template: template,
    controller: function($scope) {
      $scope.hideSpinner = function() {
        $scope.isShow = false;
      };
    }
  };
});
