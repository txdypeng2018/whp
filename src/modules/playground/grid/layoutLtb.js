(function(app) {
  'use strict';

  var gridLayoutLtbCtrl = function($scope) {
    $scope.gridParamP = {
      columns: [
        {field:'name', title: '姓名', width: 100},
        {field:'jobNum', title: '工号', width: 100},
        {field:'dept', title: '科室', width: 150}
      ],
      url: '/playground/grid/layoutLtb/person',
      title: '医师',
      multiSelect: false,
      onSelect: function() {
        $scope.gridParamC.loadData();
        $scope.gridParamL.loadData();
      },
      onDeselect: function() {
        $scope.gridParamC.loadData();
        $scope.gridParamL.loadData();
      }
    };

    $scope.gridParamC = {
      columns: [
        {field:'name', title: '证书名称', width: 150},
        {field:'loan', title: '是否外借', width: 80},
        {field:'range', title: '执业范围', width: 150},
        {field:'validity', title: '有效期', width: 200}
      ],
      url: '/playground/grid/layoutLtb/certificate',
      title: '证书信息',
      multiSelect: false,
      height: 'auto',
      pagination: false,
      onSelect: function() {
        $scope.gridParamL.loadData();
      },
      onDeselect: function() {
        $scope.gridParamL.loadData();
      },
      onBeforeLoad: function(param){
        if ($scope.gridParamP.selected.length === 1) {
          param.personId = $scope.gridParamP.selected[0].id;
        }
      }
    };

    $scope.gridParamL = {
      columns: [
        {field:'name', title: '外借人', width: 100},
        {field:'date', title: '外借时间', width: 120},
        {field:'giveBack', title: '是否归还', width: 100}
      ],
      url: '/playground/grid/layoutLtb/loan',
      title: '外借信息',
      multiSelect: false,
      height: 'auto',
      pagination: false,
      onBeforeLoad: function(param){
        if ($scope.gridParamP.selected.length === 1) {
          param.personId = $scope.gridParamP.selected[0].id;
        }
        if ($scope.gridParamC.selected.length === 1) {
          param.certificateId = $scope.gridParamC.selected[0].id;
        }
      }
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('main.playgroundGridLayoutLtb', {
      url: '^/playground/grid/layoutLtb',
      templateUrl: 'modules/playground/grid/layoutLtb.html',
      controller: gridLayoutLtbCtrl
    });
  };
  app.config(mainRouter);
})(angular.module('pea'));
