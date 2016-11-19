(function(app) {
  'use strict';

  var gridBasicCtrl = function($scope) {
    $scope.gridParam = {
      columns: [
        {field:'name', title: '姓名', width: 100},
        {field:'age', title: '年龄', width: 80, isNumeric: true},
        {field:'dept', title: '科室', width: 240},
        {field:'title', title: '职称', width: 200},
        {field:'education', title: '学历', width: 200},
        {field:'job', title: '职务', width: 200}
      ],
      url: '/playground/grid/basic',
      toolbar: false,
      rowSelection: false,
      pagination: false
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('main.playgroundGridBasic', {
      url: '^/playground/grid/basic',
      templateUrl: 'modules/playground/grid/basic.html',
      controller: gridBasicCtrl
    });
  };
  app.config(mainRouter);
})(angular.module('pea'));
