'use strict';

(function(app) {

  app.directive('treeGrid', function() {
    return {
      link: function($scope) {
        $scope.gridOptions = $scope.gridOptions || {};

        $scope.gridOptions.selected = $scope.gridOptions.selected || [];
        $scope.gridOptions.query = $scope.gridOptions.query || {};
        $scope.gridOptions.query.page = $scope.gridOptions.query.page || 1;
        $scope.gridOptions.query.limit = $scope.gridOptions.query.limit || 10;
        $scope.gridOptions.createable = $scope.gridOptions.createable === undefined ? true : $scope.gridOptions.createable;
        $scope.gridOptions.filterable = $scope.gridOptions.filterable === undefined ? true : $scope.gridOptions.filterable;
        $scope.gridOptions.editable = $scope.gridOptions.editable === undefined ? true : $scope.gridOptions.editable;
        $scope.gridOptions.deleteable = $scope.gridOptions.deleteable === undefined ? true : $scope.gridOptions.deleteable;

        var needToImp = function($event) {
          console.debug('need to implement this method. %o', $event);
        };
        $scope.gridOptions.addItem = $scope.gridOptions.addItem || needToImp;
        $scope.gridOptions.editItem = $scope.gridOptions.editItem || needToImp;
        $scope.gridOptions.delItem = $scope.gridOptions.delItem || needToImp;
        $scope.gridOptions.onReorder = $scope.gridOptions.onReorder || needToImp;
        $scope.gridOptions.onPaginate = $scope.gridOptions.onPaginate || needToImp;
      }
    };
  });

})(angular.module('pea'));
