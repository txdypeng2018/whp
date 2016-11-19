(function(app) {
  'use strict';

  var gridOperationCtrl = function($scope, $http, $mdDialog, $mdToast) {

    var searchData = function() {
      $scope.gridParam.showSearchBar();
    };

    var addData = function() {
      $mdDialog.show({
        controller: function($scope) {
          $scope.isSubmit = false;

          $scope.cancel = function() {
            $mdDialog.cancel();
          };

          $scope.submit = function() {
            if ($scope.rulesForm.$valid) {
              $scope.isSubmit = true;
              $http.post('/registration/rules', $scope.rules).success(function(data) {
                if (angular.isUndefined(data.errMsg)) {
                  $mdDialog.hide();
                }
                else {
                  $scope.isSubmit = false;
                  $mdToast.show($mdToast.simple().textContent(data.errMsg));
                }
              });
            }
          };
        },
        templateUrl: 'modules/registration/rulesDialog.html',
        parent: angular.element(document.body),
        fullscreen: true
      }).then(function() {
        $scope.gridParam.loadData();
      });
    };

    var editData = function(rowDatas) {
      $mdDialog.show({
        controller: function($scope) {
          $http.get('/registration/rules/' + rowDatas[0].id).success(function(data) {
            $scope.rules = data;
          });

          $scope.isSubmit = false;

          $scope.cancel = function() {
            $mdDialog.cancel();
          };

          $scope.submit = function() {
            if ($scope.rulesForm.$valid) {
              $scope.isSubmit = true;
              $http.put('/registration/rules', $scope.rules).success(function(data) {
                if (angular.isUndefined(data.errMsg)) {
                  $mdDialog.hide();
                }
                else {
                  $scope.isSubmit = false;
                  $mdToast.show($mdToast.simple().textContent(data.errMsg));
                }
              });
            }
          };
        },
        templateUrl: 'modules/registration/rulesDialog.html',
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
        $http.delete('/registration/rules', {params: {ids: ids.join(',')}}).success(function(data) {
          if (angular.isUndefined(data.errMsg)) {
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
        {field:'catalogue', title: '挂号规则分类', width: 160},
        {field:'name', title: '挂号规则名称', width: 180},
        {field:'rule', title: '挂号规则内容', width: 800}
      ],
      url: '/registration/rules',
      operations: [
        {text: '查询', click: searchData, iconClass: 'search_black', display: ['unSelected']},
        {text: '新增', click: addData, iconClass: 'add_black', display: ['unSelected']},
        {text: '编辑', click: editData, iconClass: 'edit_black', display: ['singleSelected']},
        {text: '删除', click: deleteData, iconClass: 'delete_black', display: ['singleSelected', 'multipleSelected']}
      ],
      title: '挂号规则信息',
      multiSelect: true,
      isAllDataLoad: false,
      searchTemplate: 'modules/registration/rulesSearch.html',
      searchInitData: {
        statusCode: $scope.opStatus
      }
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('main.rulesGridOperation', {
      url: '^/registration/rulesn',
      templateUrl: 'modules/registration/rules.html',
      controller: gridOperationCtrl
    });
  };
  app.config(mainRouter);
})(angular.module('pea'));
