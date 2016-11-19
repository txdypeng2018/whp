(function(app) {
  'use strict';

  var gridOrderCtrl = function($scope) {
    $scope.gridParam = {
      columns: [
        {field:'name', title: '姓名', width: 100, enableSort: 'name'},
        {field:'age', title: '年龄', width: 80, isNumeric: true, enableSort: 'age', descFirst: true},
        {field:'dept', title: '科室', width: 240},
        {field:'title', title: '职称', width: 200},
        {field:'education', title: '学历', width: 200},
        {field:'job', title: '职务', width: 200},
        {field:'remark', title: '备注', width: 260}
      ],
      url: '/playground/grid/basic',
      title: '职工信息',
      rowSelection: false,
      order: 'name'
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('main.playgroundGridOrder', {
      url: '^/playground/grid/order',
      templateUrl: 'modules/playground/grid/basic.html',
      controller: gridOrderCtrl
    });
  };
  app.config(mainRouter);
})(angular.module('pea'));
