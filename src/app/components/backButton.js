'use strict';

angular.module('isj').directive('isjBackButton', function() {
  return {
    restrict: 'E',
    replace: true,
    scope: {
      goBack: '&?'
    },
    template: '<a class="icon-return positive" ng-click="goBack()"><i class="icon ion-ios-arrow-left"></i> <div>返回</div></a>',
    controller:function($scope, $ionicHistory) {
      if (angular.isUndefined($scope.goBack)) {
        $scope.goBack = function() {
           var currentView = $ionicHistory.currentView();
            /*  if(currentView.viewId.substring(3,4)!=='1'){
             $ionicHistory.currentView().backViewId = 'ion'+(parseInt(currentView.viewId.substring(3,4))-1).toString();
             }*/
             console.log(currentView);
             console.log(parseInt(currentView.viewId.substring(3,4)));
          $ionicHistory.goBack();
        };
      }
    }
  };
});
