'use strict';

(function () {
  angular.module('pea').directive('peaRepeatFinish', function($timeout) {
    return {
      link: function(scope, element, attr){
        if(scope.$last === true){
          $timeout(function() {
            scope.$eval(attr.peaRepeatFinish);
          });
        }
      }
    };
  });
})();