(function(app) {
  'use strict';

  var gridLayoutLrCtrl = function($scope) {
    $scope.gridParamType = {
      columns: [
        {field:'name', title: '名称', width: 120},
        {field:'code', title: '代码', width: 150},
        {field:'status', title: '状态', width: 100, formatter: function(value){
          if (value === '1') {
            return '启用';
          }
          else {
            return '停用';
          }
        }}
      ],
      url: '/playground/grid/layoutLr/type',
      title: '类别',
      multiSelect: false,
      onSelect: function() {
        $scope.gridParamItem.loadData();
      },
      onDeselect: function() {
        $scope.gridParamItem.loadData();
      }
    };

    $scope.gridParamItem = {
      columns: [
        {field:'name', title: '名称', width: 120},
        {field:'code', title: '代码', width: 150},
        {field:'status', title: '状态', width: 100, formatter: function(value){
          if (value === '1') {
            return '启用';
          }
          else {
            return '停用';
          }
        }},
        {field:'orderNum', title: '排序号', width: 150}
      ],
      url: '/playground/grid/layoutLr/item',
      title: '项目',
      multiSelect: false,
      onBeforeLoad: function(param){
        if ($scope.gridParamType.selected.length === 1) {
          param.typeCode = $scope.gridParamType.selected[0].code;
        }
      }
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('main.playgroundGridLayoutLr', {
      url: '^/playground/grid/layoutLr',
      templateUrl: 'modules/playground/grid/layoutLr.html',
      controller: gridLayoutLrCtrl
    });
  };
  app.config(mainRouter);
})(angular.module('pea'));
