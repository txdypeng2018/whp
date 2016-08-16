'use strict';

angular.module('isj').directive('isjSpinner', function() {
  return {
    restrict: 'E',
    replace: true,
    scope:{
      isShow: '=isShow'
    },
    template: '<div ng-if="isShow" class="isj-spinner"><ion-spinner icon="android" class="spinner-positive"></ion-spinner><span class="positive">加载中...</span></div>'
  };
});
