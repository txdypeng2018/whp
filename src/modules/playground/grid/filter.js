(function(app) {
  'use strict';

  var gridFilterCtrl = function($scope) {
    var educations = [{id:'1',name:'专科'},{id:'2',name:'本科'}];

    var searchData = function() {
      $scope.gridParam.showSearchBar();
    };

    $scope.gridParam = {
      columns: [
        {field:'name', title: '姓名', width: 100},
        {field:'age', title: '年龄', width: 80, isNumeric: true},
        {field:'dept', title: '科室', width: 240},
        {field:'title', title: '职称', width: 200},
        {field:'education', title: '学历', width: 200},
        {field:'job', title: '职务', width: 200},
        {field:'remark', title: '备注', width: 260}
      ],
      url: '/playground/grid/basic',
      operations: [
        {text: '查询', click: searchData, iconClass: 'search_black', display: ['unSelected']}
      ],
      title: '职工信息',
      rowSelection: false,
      hasFilter: true,
      searchTemplate: 'modules/playground/grid/operationSearch.html',
      searchInitData: {
        educations: educations
      }
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('main.playgroundGridFilter', {
      url: '^/playground/grid/filter',
      templateUrl: 'modules/playground/grid/basic.html',
      controller: gridFilterCtrl
    });
  };
  app.config(mainRouter);
})(angular.module('pea'));
