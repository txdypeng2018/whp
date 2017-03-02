(function(app) {
  'use strict';

  var gridOperationCtrl = function($scope, $http, $mdDialog, $mdToast) {
    var educations = [{id:'1',name:'专科'},{id:'2',name:'本科'}];

    var searchData = function() {
      $scope.gridParam.showSearchBar();
    };

    var addData = function() {
      $mdDialog.show({
        controller: function($scope) {
          $scope.isSubmit = false;
          $scope.educations = educations;

          $scope.cancel = function() {
            $mdDialog.cancel();
          };

          $scope.submit = function() {
            if ($scope.userForm.$valid) {
              $scope.isSubmit = true;
              $http.post('/playground/grid/basic/data', $scope.user).then(function(response) {
                if (angular.isUndefined(response.data.errMsg)) {
                  $mdDialog.hide();
                }
                else {
                  $scope.isSubmit = false;
                  $mdToast.show($mdToast.simple().textContent(response.data.errMsg));
                }
              });
            }
          };
        },
        templateUrl: 'modules/playground/grid/operationDialog.html',
        parent: angular.element(document.body),
        fullscreen: true
      }).then(function() {
        $scope.gridParam.loadData();
      });
    };

    var editData = function(rowDatas) {
      $mdDialog.show({
        controller: function($scope) {
          $http.get('/playground/grid/basic/data', {params: {id: rowDatas[0].id}}).then(function(response) {
            $scope.user = response.data;
          });

          $scope.isSubmit = false;
          $scope.educations = educations;

          $scope.cancel = function() {
            $mdDialog.cancel();
          };

          $scope.submit = function() {
            if ($scope.userForm.$valid) {
              $scope.isSubmit = true;
              $http.put('/playground/grid/basic/data', $scope.user).then(function(response) {
                if (angular.isUndefined(response.data.errMsg)) {
                  $mdDialog.hide();
                }
                else {
                  $scope.isSubmit = false;
                  $mdToast.show($mdToast.simple().textContent(response.data.errMsg));
                }
              });
            }
          };
        },
        templateUrl: 'modules/playground/grid/operationDialog.html',
        parent: angular.element(document.body),
        fullscreen: true
      }).then(function() {
        $scope.gridParam.loadData();
      });
    };

    var deleteData = function(rowDatas) {
      $mdDialog.show($mdDialog.confirm().title('确定删除选中的记录？').ok('确定').cancel('取消')).then(function() {
        var ids = [];
        for(var i in rowDatas) {
          ids.push(rowDatas[i].id);
        }
        $http.delete('/playground/grid/basic/data', {params: {ids: ids.join(',')}}).then(function(response) {
          if (angular.isUndefined(response.data.errMsg)) {
            $scope.gridParam.loadData();
          }
          else {
            $mdToast.show($mdToast.simple().textContent(response.data.errMsg));
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
        {text: '查询', click: searchData, iconClass: 'search_black', display: ['unSelected']},
        {text: '新增', click: addData, iconClass: 'add_black', display: ['unSelected']},
        {text: '编辑', click: editData, iconClass: 'edit_black', display: ['singleSelected']},
        {text: '删除', click: deleteData, iconClass: 'delete_black', display: ['singleSelected', 'multipleSelected']}
      ],
      title: '职工信息',
      multiSelect: true,
      isAllDataLoad: false,
      mouseEnterDisplayAll: true,
      searchTemplate: 'modules/playground/grid/operationSearch.html',
      searchInitData: {
        educations: educations
      }
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('main.playgroundGridOperation', {
      url: '^/playground/grid/operation',
      templateUrl: 'modules/playground/grid/basic.html',
      controller: gridOperationCtrl
    });
  };
  app.config(mainRouter);
})(angular.module('pea'));
