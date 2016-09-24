'use strict';

var dateSelectCtrl = function() {

};

angular.module('isj').directive('isjDateSelect', function() {
  return {
    restrict: 'E',
    replace: false,
    scope:{
      dateSelectParam: '=dateSelectParam',
      callback: '&'
    },
    templateUrl: 'app/components/date-select.html',
    controller: dateSelectCtrl
  };
});
