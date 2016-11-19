(function(app) {
  'use strict';

  var gridColumnShowCtrl = function($scope) {
    $scope.gridParam = {
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
      title: '职工信息',
      rowSelection: false,
      columnCustom: 'playgroundGridColumnShow'
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('main.playgroundGridColumnShow', {
      url: '^/playground/grid/columnShow',
      templateUrl: 'modules/playground/grid/basic.html',
      controller: gridColumnShowCtrl
    });
  };
  app.config(mainRouter);
})(angular.module('pea'));
