(function(app) {
  'use strict';

  var gridFormatterCtrl = function($scope) {
    $scope.gridParam = {
      columns: [
        {field:'name', title: '姓名', width: 100},
        {field:'age', title: '年龄', width: 80, isNumeric: true},
        {field:'dept', title: '科室', width: 240},
        {field:'title', title: '职称', width: 200},
        {field:'education', title: '学历', width: 200, formatter: function(value){
          return  'XX大学'+value;
        }},
        {field:'job', title: '职务', width: 200},
        {field:'remark', title: '备注', width: 260, isLineWrap: true}
      ],
      url: '/playground/grid/basic',
      title: '职工信息',
      rowSelection: false
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('main.playgroundGridFormatter', {
      url: '^/playground/grid/formatter',
      templateUrl: 'modules/playground/grid/basic.html',
      controller: gridFormatterCtrl
    });
  };
  app.config(mainRouter);
})(angular.module('pea'));
