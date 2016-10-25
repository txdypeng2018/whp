'use strict';

(function () {
  angular.module('iosDblclick', []).directive('iosDblclick', function() {
    var DblClickInterval = 300;
    var firstClickTime;
    var waitingSecondClick = false;

    return {
      restrict: 'A',
      link: function (scope, element, attrs) {
        element.bind('click', function () {
          if (!waitingSecondClick) {
            firstClickTime = (new Date()).getTime();
            waitingSecondClick = true;
            setTimeout(function () {
              waitingSecondClick = false;
            }, DblClickInterval);
          }
          else {
            waitingSecondClick = false;
            var time = (new Date()).getTime();
            if (time - firstClickTime < DblClickInterval) {
              scope.$apply(attrs.iosDblclick);
            }
          }
        });
      }
    };
  });
})();
