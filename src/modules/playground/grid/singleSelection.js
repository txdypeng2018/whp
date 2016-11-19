(function(app) {
  'use strict';

  var gridSingleSelectionCtrl = function($scope, $http, $mdDialog, $mdToast) {
    var deleteData = function(rowDatas) {
      $mdDialog.show($mdDialog.confirm().title('确定删除选中的记录？').ok('确定').cancel('取消')).then(function() {
        var ids = [];
        for(var i in rowDatas) {
          ids.push(rowDatas[i].id);
        }
        $http.delete('/playground/grid/basic/data', {params: {ids: ids.join(',')}}).success(function(data) {
          if (typeof(data.errMsg) === 'undefined') {
            $scope.gridParam.loadData();
          }
          else {
            $mdToast.show($mdToast.simple().textContent(data.errMsg));
          }
        });
      });
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
        {text: '删除', click: deleteData, iconClass: 'delete_black', display: ['singleSelected']}
      ],
      title: '职工信息'
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('main.playgroundGridSingleSelection', {
      url: '^/playground/grid/singleSelection',
      templateUrl: 'modules/playground/grid/basic.html',
      controller: gridSingleSelectionCtrl
    });
  };
  app.config(mainRouter);
})(angular.module('pea'));
