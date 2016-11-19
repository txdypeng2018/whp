(function(app) {
  'use strict';

  var gridHeadsCtrl = function($scope) {
    var searchData = function() {
      $scope.gridParam.showSearchBar();
    };

    $scope.gridParam = {
      typeColumns: [[
        {title: '', colspan: 2},
        {title: '职位', colspan: 2},
        {title: '', colspan: 3}
      ]],
      columns: [
        {field:'name', title: '姓名', width: 100},
        {field:'age', title: '年龄', width: 80, isNumeric: true},
        {field:'dept', title: '科室', width: 240},
        {field:'job', title: '职务', width: 200},
        {field:'title', title: '职称', width: 200},
        {field:'education', title: '学历', width: 200},
        {field:'remark', title: '备注', width: 260}
      ],
      url: '/playground/grid/basic',
      operations: [
        {text: '查询', click: searchData, iconClass: 'search_black', display: ['unSelected']}
      ],
      title: '职工信息',
      rowSelection: false,
      hasFilter: true
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('main.playgroundGridHeads', {
      url: '^/playground/grid/heads',
      templateUrl: 'modules/playground/grid/basic.html',
      controller: gridHeadsCtrl
    });
  };
  app.config(mainRouter);
})(angular.module('pea'));
