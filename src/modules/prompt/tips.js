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
            if ($scope.tipsForm.$valid) {
              $scope.isSubmit = true;
              $http.post('/prompt/tips', $scope.tips).success(function(data) {
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
        templateUrl: 'modules/prompt/tipsDialog.html',
        parent: angular.element(document.body),
        fullscreen: true
      }).then(function() {
        $scope.gridParam.loadData();
      });
    };

    var editData = function(rowDatas) {
      $mdDialog.show({
        controller: function($scope) {
          $http.get('/prompt/tips/' + rowDatas[0].id).success(function(data) {
            $scope.tips = data;
          });

          $scope.isSubmit = false;

          $scope.cancel = function() {
            $mdDialog.cancel();
          };

          $scope.submit = function() {
            if ($scope.tipsForm.$valid) {
              $scope.isSubmit = true;
              $http.put('/prompt/tips', $scope.tips).success(function(data) {
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
        templateUrl: 'modules/prompt/tipsDialog.html',
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
        $http.delete('/prompt/tips', {params: {ids: ids.join(',')}}).success(function(data) {
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
        {field:'infoType', title: '温馨提示类型编码', width: 160},
        {field:'typeName', title: '温馨提示类型名称', width: 180},
        {field:'info', title: '温馨提示内容', width: 800}
      ],
      url: '/prompt/tips',
      operations: [
        {text: '查询', click: searchData, iconClass: 'search_black', display: ['unSelected']},
        {text: '新增', click: addData, iconClass: 'add_black', display: ['unSelected']},
        {text: '编辑', click: editData, iconClass: 'edit_black', display: ['singleSelected']},
        {text: '删除', click: deleteData, iconClass: 'delete_black', display: ['singleSelected', 'multipleSelected']}
      ],
      title: '意见反馈信息',
      multiSelect: true,
      isAllDataLoad: false,
      searchTemplate: 'modules/prompt/tipsSearch.html',
      searchInitData: {
        statusCode: $scope.opStatus
      }
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('main.tipsGridOperation', {
      url: '^/prompt/tips',
      templateUrl: 'modules/prompt/tips.html',
      controller: gridOperationCtrl
    });
  };
  app.config(mainRouter);
})(angular.module('pea'));
