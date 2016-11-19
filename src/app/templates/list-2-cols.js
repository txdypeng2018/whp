'use strict';

(function(app) {

  app.directive('list2cols', function($timeout) {
    return {
      link: function($scope) {
        $scope.clickEnter = $scope.clickEnter || function() {
          var actEleId = document.activeElement.id;
          if (actEleId === 'qs') {
            $scope.qsQuery();
          }
        };

        $scope.clickESC = $scope.clickESC || function() {
          var ele = document.getElementById('qs');
          if (ele) {
            ele.blur();
          }
        };

        $scope.focusQS = $scope.focusQS || function() {
          $timeout(function() {
            document.getElementById('qs').focus();
          }, 100);
        };

        $scope.showMenus = $scope.showMenus || function() {
          $scope.isMenusOpen = !$scope.isMenusOpen;
        };

        $scope.resetList = $scope.resetList || function() {
          if (!document.getElementById('qs').value) {
            $scope.qsQuery();
          }
        };
      }
    };
  });

})(angular.module('pea'));
