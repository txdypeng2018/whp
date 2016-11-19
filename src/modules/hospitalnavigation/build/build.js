(function(app) {
  'use strict';

  var buildGridLayoutLrCtrl = function($scope, $http, $mdDialog, $mdToast) {
    var floorParentId = '';
    //取得院区列表
    $scope.district = [];
    $scope.districtCodes = [];
    $scope.district.push({
      id: '',
      name: '全部'
    });
    $http.get('/organization/districts').success(function (data) {
      for (var i = 0; i < data.length; i++) {
        $scope.district.push(data[i]);
        $scope.districtCodes.push(data[i]);
      }
    }).error(function (data) {
      $mdToast.show($mdToast.simple().textContent(data));
    });
    var districtCodes = $scope.districtCodes;
    //左侧检索
    var leftSearchData = function() {
      $scope.gridParamType.showSearchBar();
    };
    //右侧检索
    var rightSearchData = function() {
      $scope.gridParamItem.showSearchBar();
    };
    // 左侧楼宇添加
    var leftAddData = function() {
      $mdDialog.show({
        controller: function($scope) {
          $scope.isSubmit = false;
          $scope.districts = districtCodes;
          $scope.showDis = true;
          $scope.build = {};
          $scope.build.districtCode = districtCodes[0].id;

          $scope.cancel = function() {
            $mdDialog.cancel();
          };

          $scope.submit = function() {
            if ($scope.buildForm.$valid) {
              $scope.isSubmit = true;
              $http.post('/hospitalNavigation/build', $scope.build).success(function(data) {
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
        templateUrl: 'modules/hospitalNavigation/build/leftDialog.html',
        parent: angular.element(document.body),
        fullscreen: true
      }).then(function() {
        $scope.gridParamType.loadData();
      });
    };
    //右侧楼层科室添加
    var rightAddData = function() {
      if(floorParentId === '') {
        $mdToast.show($mdToast.simple().textContent('请选择院区与楼宇!'));
      } else {
        $mdDialog.show({
          controller: function($scope) {
            $scope.isSubmit = false;
            $scope.showFloor = false;
            $scope.floor = {};
            $scope.floor.floorParentId = floorParentId;

            $scope.cancel = function() {
              $mdDialog.cancel();
            };

            $scope.submit = function() {
              if ($scope.floorForm.$valid) {
                $scope.isSubmit = true;
                $http.post('/hospitalNavigation/floor', $scope.floor).success(function(data) {
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
          templateUrl: 'modules/hospitalNavigation/build/rightDialog.html',
          parent: angular.element(document.body),
          fullscreen: true
        }).then(function() {
          $scope.gridParamType.loadData();
        });
      }
    };
    //左侧楼宇编辑
    var leftEditData = function(rowDatas) {
      $mdDialog.show({
        controller: function($scope) {
          $http.get('/hospitalNavigation/build/' + rowDatas[0].buildingCode).success(function(data) {
            $scope.build = data;
          });

          $scope.isSubmit = false;
          $scope.showDis = false;
          $scope.districts = districtCodes;

          $scope.cancel = function() {
            $mdDialog.cancel();
          };

          $scope.submit = function() {
            if ($scope.buildForm.$valid) {
              $scope.isSubmit = true;
              $http.put('/hospitalNavigation/build', $scope.build).success(function(data) {
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
        templateUrl: 'modules/hospitalNavigation/build/leftDialog.html',
        parent: angular.element(document.body),
        fullscreen: true
      }).then(function() {
        $scope.gridParamItem.loadData();
      });
    };
    //右侧楼层科室编辑
    var rightEditData = function(rowDatas) {
      $mdDialog.show({
        controller: function($scope) {
          $http.get('/hospitalNavigation/floor/' + rowDatas[0].floorId).success(function(data) {
            $scope.floor = data;
          });

          $scope.isSubmit = false;

          $scope.cancel = function() {
            $mdDialog.cancel();
          };

          $scope.submit = function() {
            if ($scope.floorForm.$valid) {
              $scope.isSubmit = true;
              $http.put('/hospitalNavigation/floor', $scope.floor).success(function(data) {
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
        templateUrl: 'modules/hospitalNavigation/build/rightDialog.html',
        parent: angular.element(document.body),
        fullscreen: true
      }).then(function() {
        $scope.gridParamItem.loadData();
      });
    };
    //左侧楼宇删除
    var leftDeleteData = function(rowDatas) {
      $mdDialog.show($mdDialog.confirm().title('确定删除选中的记录？').ok('确定').cancel('取消')).then(function() {
        var ids = [];
        for(var i in rowDatas) {
          ids.push(rowDatas[i].id);
        }
        $http.delete('/hospitalNavigation/build', {params: {ids: ids.join(',')}}).success(function(data) {
          if (angular.isUndefined(data.errMsg)) {
            $scope.gridParamType.loadData();
          }
          else {
            $mdToast.show($mdToast.simple().textContent(data.errMsg));
          }
        });
      });
    };
    //右侧楼层科室删除
    var rightDeleteData = function(rowDatas) {
      $mdDialog.show($mdDialog.confirm().title('确定删除选中的记录？').ok('确定').cancel('取消')).then(function() {
        var ids = [];
        for(var i in rowDatas) {
          ids.push(rowDatas[i].fid);
          ids.push(rowDatas[i].did);
        }
        $http.delete('/hospitalNavigation/floor', {params: {ids: ids.join(',')}}).success(function(data) {
          if (angular.isUndefined(data.errMsg)) {
            $scope.gridParamType.loadData();
          }
          else {
            $mdToast.show($mdToast.simple().textContent(data.errMsg));
          }
        });
      });
    };

    $scope.gridParamType = {
      columns: [
        {field:'districtCode', title: '院区', width: 120 ,formatter: function(value){
          for(var i = 0; i < $scope.district.length; i++) {
            if(value !== '' && $scope.district[i].id === value) {
              return $scope.district[i].name;
            }
          }
        }},
        //{field:'buildingCode', title: '楼宇ID', width: 100},
        {field:'buildingName', title: '楼宇名称', width: 150}
      ],
      url: '/hospitalNavigation/build',
      operations: [
        {text: '查询', click: leftSearchData, iconClass: 'search_black', display: ['unSelected']},
        {text: '新增', click: leftAddData, iconClass: 'add_black', display: ['unSelected']},
        {text: '编辑', click: leftEditData, iconClass: 'edit_black', display: ['singleSelected']},
        {text: '删除', click: leftDeleteData, iconClass: 'delete_black', display: ['singleSelected', 'multipleSelected']}
      ],
      title: '楼宇信息',
      searchTemplate: 'modules/hospitalNavigation/build/leftSearch.html',
      searchInitData: {
        districtCode: $scope.district
      },
      multiSelect: false,
      onSelect: function() {
        //$scope.gridParamItem.searchData.buildId = $scope.gridParamType.selected[0].buildingCode;
        $scope.gridParamItem.loadData();
      },
      onDeselect: function() {
        //$scope.gridParamItem.searchData.buildId = '';
        floorParentId = '';
        $scope.gridParamItem.loadData();
      }
    };

    $scope.gridParamItem = {
      columns: [
        //{field:'floorId', title: '楼层ID', width: 120},
        {field:'floorName', title: '楼层名称', width: 100},
        //{field:'deptId', title: '科室ID', width: 120},
        {field:'deptName', title: '科室名称', width: 800}
      ],
      url: '/hospitalNavigation/floor',
      operations: [
        {text: '查询', click: rightSearchData, iconClass: 'search_black', display: ['unSelected']},
        {text: '新增', click: rightAddData, iconClass: 'add_black', display: ['unSelected']},
        {text: '编辑', click: rightEditData, iconClass: 'edit_black', display: ['singleSelected']},
        {text: '删除', click: rightDeleteData, iconClass: 'delete_black', display: ['singleSelected', 'multipleSelected']}
      ],
      title: '楼层科室信息',
      searchTemplate: 'modules/hospitalNavigation/build/rightSearch.html',
      multiSelect: false,
      onBeforeLoad: function(param){
        if ($scope.gridParamType.selected.length === 1) {
          param.floorParentId = $scope.gridParamType.selected[0].buildingCode;
          if(param.deptName === undefined || param.deptName === '') {
            param.deptName = '';
          }
          floorParentId = $scope.gridParamType.selected[0].buildingCode;
        }
      }
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('main.buildGridLayoutLr', {
      url: '^/hospitalnavigation/build',
      templateUrl: 'modules/hospitalNavigation/build/build.html',
      controller: buildGridLayoutLrCtrl
    });
  };
  app.config(mainRouter);
})(angular.module('pea'));
