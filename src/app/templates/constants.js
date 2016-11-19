'use strict';

(function(app) {
  var PEA = {};

  PEA.tpls = {

    /**
     * Need to define below things in the scope of controller using this template:
     *
     *  - {Function} qsQuery:           Do query for the list and set results to $scope.items
     *  - {Array}    items:             To store query results
     *  - {Function} clickListItem:     Emit on click list item
     *  - {Function} listItemTemplate:  The template of show item in list
     *  - {Boolean}  showMoreListItems: Is need to show more items
     *  - {Function} getMoreListItems:  Function to get more items
     *  - {String}   contentPageUrl:    The url of right part content page
     *  - {Array}    fabBtns:           FAB speed dial buttons array, button object is like this:
     *               {
   *                 ariaLabel: [label text],
   *                 click:     [click function],
   *                 tooltip:   [tooltip text],
   *                 icon:      [icon name]
   *               }
     *  - {Function} createItem:        Used by default hot keys
     *  - {Function} editItem:          Used by default hot keys
     *  - {Function} delItem:           Used by default hot keys
     *
     * Example code:
     *
     *  var ctrl = function($scope) {
   *    $scope.qsQuery = function() {};
   *    $scope.items = [];
   *    $scope.clickListItem = function(item) {};
   *    $scope.listItemTemplate = function(item) {};
   *    $scope.showMoreListItems = true;
   *    $scope.getMoreListItems = function() {};
   *    $scope.contentPageUrl = '';
   *    $scope.fabBtns = [{
   *      ariaLabel: 'label',
   *      tooltip: 'text',
   *      icon: 'add',
   *      click: function() {}
   *    }];
   *    $scope.createItem = function() {};
   *    $scope.editItem = function() {};
   *    $scope.delItem = function() {};
   *  }
     *
     *  var router = function($stateProvider) {
   *    $stateProvider.state('main.fooBar', {
   *      url: '^/foo/bar',
   *      controller: ctrl,
   *      templateUrl: window.tpls.list2Cols
   *    });
   *  };
     *
     *  app.config(router);
     */
    list2Cols: 'app/templates/list-2-cols.html',

    treeGrid: 'app/templates/tree-grid.html'

  };

  app.constant('PEA', PEA);
})(angular.module('pea'));
