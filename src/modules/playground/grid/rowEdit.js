(function(app) {
  'use strict';

  var gridRowEditCtrl = function($scope, $http, $mdToast, $mdEditDialog) {
    var educations = [{id:'专科',name:'专科'},{id:'本科',name:'本科'}];

    var addData = function() {
      $scope.gridParam.showRowEdit();
    };

    var rowEditSubmit = function() {
      if ($scope.gridParam.rowEditFrom().$valid) {
        $scope.gridParam.disableRowEditSubmit(true);
        $http.post('/playground/grid/basic/data', $scope.gridParam.rowEditData).success(function(data) {
          if (angular.isUndefined(data.errMsg)) {
            $scope.gridParam.loadData();
            $scope.gridParam.closeRowEdit();
          }
          else {
            $scope.gridParam.disableRowEditSubmit(false);
            $mdToast.show($mdToast.simple().textContent(data.errMsg));
          }
        });
      }
    };

    var columnEdit = function(rowData, field, event) {
      event.stopPropagation();

      var columnTitle = {name: '姓名', age: '年龄', dept: '科室', job: '职务', title: '职称', education: '学历', remark: '备注'};
      var template = '<md-edit-dialog>';
      template += '<form name="form1" class="md-content" ng-submit="submit()">';
      template += '<md-input-container md-no-float class="md-block">';
      if (field === 'education') {
        template += '<md-select md-autofocus ng-model="model" placeholder="学历">';
        template += '<md-option ng-repeat="education in educations" value="{{education.id}}">{{education.name}}</md-option>';
        template += '</md-select>';
        template += '<div class="md-errors-spacer"></div>';
      }
      else {
        template += '<input md-autofocus ng-model="model" placeholder="'+columnTitle[field]+'"';
        if (field === 'name') {
          template += ' required';
        }
        else if (field === 'age') {
          template += ' type="number"';
        }
        else if (field === 'remark') {
          template += ' md-maxlength="30"';
        }
        template += '>';
      }
      template += '</md-input-container>';
      template += '</form>';
      template += '</md-edit-dialog>';

      $mdEditDialog.show({
        controller: ['$scope', '$element', function($scope, $element) {
          var isSubmit = true;

          var putData = function() {
            isSubmit = false;
            var bak = rowData[field];
            rowData[field] = $scope.model;
            $http.put('/playground/grid/basic/data', rowData).success(function(data) {
              if (angular.isUndefined(data.errMsg)) {
                $element.remove();
              }
              else {
                isSubmit = true;
                rowData[field] = bak;
                $mdToast.show($mdToast.simple().textContent(data.errMsg));
              }
            });
          };

          if (field === 'education') {
            $scope.$watch('model', function ($newValue, $oldValue) {
              if (!angular.isUndefined($newValue) && $newValue !== $oldValue) {
                putData();
              }
            });
          }
          $scope.submit = function() {
            if ($scope.form1.$valid && isSubmit) {
              putData();
            }
          };
        }],
        targetEvent: event,
        scope: {
          model: rowData[field],
          educations: educations
        },
        template: template
      });
    };

    $scope.gridParam = {
      columns: [
        {field:'name', title: '姓名', width: 100, edit: columnEdit},
        {field:'age', title: '年龄', width: 80, isNumeric: true, edit: columnEdit},
        {field:'dept', title: '科室', width: 240, edit: columnEdit},
        {field:'job', title: '职务', width: 200, edit: columnEdit},
        {field:'title', title: '职称', width: 200, edit: columnEdit},
        {field:'education', title: '学历', width: 200, edit: columnEdit},
        {field:'remark', title: '备注', width: 260, edit: columnEdit}
      ],
      url: '/playground/grid/basic',
      operations: [
        {text: '新增', click: addData, iconClass: 'add_black', display: ['unSelected']}
      ],
      title: '职工信息',
      rowEditTemplate: 'modules/playground/grid/rowEdit.html',
      rowEditInitData: {
        educations: educations
      },
      rowEditSubmit: rowEditSubmit
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('main.playgroundGridRowEdit', {
      url: '^/playground/grid/rowEdit',
      templateUrl: 'modules/playground/grid/basic.html',
      controller: gridRowEditCtrl
    });
  };
  app.config(mainRouter);
})(angular.module('pea'));
