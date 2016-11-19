(function(app) {
  'use strict';

  var districtGridOperationCtrl = function($scope, $http, $mdDialog, $mdToast) {

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
            if ($scope.districtForm.$valid) {
              $scope.isSubmit = true;
              $http.post('/hospitalnavigation/district', $scope.district).success(function(data) {
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
        templateUrl: 'modules/hospitalnavigation/district/districtDialog.html',
        parent: angular.element(document.body),
        fullscreen: true
      }).then(function() {
        $scope.gridParam.loadData();
      });
    };

    var editData = function(rowDatas) {
      $mdDialog.show({
        controller: function($scope) {
          $http.get('/hospitalnavigation/district/' + rowDatas[0].id).success(function(data) {
            $scope.district = data;
          });

          $scope.isSubmit = false;

          $scope.cancel = function() {
            $mdDialog.cancel();
          };

          $scope.submit = function() {
            if ($scope.districtForm.$valid) {
              $scope.isSubmit = true;
              $http.put('/hospitalnavigation/district', $scope.district).success(function(data) {
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
        templateUrl: 'modules/hospitalnavigation/district/districtDialog.html',
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
        $http.delete('/hospitalnavigation/district', {params: {ids: ids.join(',')}}).success(function(data) {
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
        {field:'navId', title: '院区ID', width: 140},
        {field:'navName', title: '院区名称', width: 300}
      ],
      url: '/hospitalnavigation/district',
      operations: [
        {text: '查询', click: searchData, iconClass: 'search_black', display: ['unSelected']},
        {text: '新增', click: addData, iconClass: 'add_black', display: ['unSelected']},
        {text: '编辑', click: editData, iconClass: 'edit_black', display: ['singleSelected']},
        {text: '删除', click: deleteData, iconClass: 'delete_black', display: ['singleSelected', 'multipleSelected']}
      ],
      title: '院区信息',
      multiSelect: true,
      isAllDataLoad: false,
      searchTemplate: 'modules/hospitalnavigation/district/districtSearch.html',
      searchInitData: {
        statusCode: $scope.opStatus
      }
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('main.districtGridOperation', {
      url: '^/hospitalnavigation/district',
      templateUrl: 'modules/hospitalnavigation/district/district.html',
      controller: districtGridOperationCtrl
    });
  };
  app.config(mainRouter);
})(angular.module('pea'));
