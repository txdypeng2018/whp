'use strict';

(function(app) {

  app.directive('autoFocus', function($timeout) {
    return {
      restrict: 'AC',
      compile: function() {
        return function(_scope, _element, _attrs) {
          var firstChild = (_attrs.focusFirstChild !== undefined);
          $timeout(function() {
            if (firstChild) {
              // Look for first input-element in child-tree and focus that
              var inputs = _element.find('input');
              if (inputs && inputs.length > 0) {
                inputs[0].focus();
              }
            } else {
              // Focus element where the directive is put on
              _element[0].focus();
            }
          }, 100);
        };
      }
    };
  });

})(angular.module('pea'));
