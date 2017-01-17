'use strict';

(function(){
  angular.module('pea.buttonToggle', [])
    .directive('peaButtonToggle', function() {
      return {
        template: '<div class="pea-button-toggle">' +
          '<md-button ng-repeat="option in items" class="md-raised" ng-class="{\'{{selectedClass}}\': option.value == ngModel, left: $index == 0, right: $last}" ng-click="buttonChange(option.value)" ng-bind="option.label" aria-label="option.label"></md-button>' +
          '</div>',
        restrict: 'E',
        replace: true,
        scope: {
          ngModel: '=',
          items: '=',
          selectedClass: '@?'
        },
        controller: function($scope) {
          if(angular.isUndefined($scope.selectedClass)){
            $scope.selectedClass = 'md-primary';
          }

          $scope.buttonChange = function(key) {
            $scope.ngModel = key;
          };
        }
      };
    });
})();
