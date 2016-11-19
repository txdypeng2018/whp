(function(app) {
  'use strict';

  var gridColumnEditCtrl = function($scope, $http, $mdEditDialog, $mdToast) {
    var educationEdit = function(rowData, field, event) {
      event.stopPropagation();

      var educations = [{id:'专科',name:'专科'},{id:'本科',name:'本科'}];
      var template = '<md-edit-dialog>' +
        '<form name="form1" class="md-content" ng-submit="submit()">' +
        '<md-input-container md-no-float class="md-block">' +
        '<md-select md-autofocus ng-model="model" placeholder="学历">' +
        '<md-option ng-repeat="education in educations" value="{{education.id}}">{{education.name}}</md-option>' +
        '</md-select>' +
        '<div class="md-errors-spacer"></div>' +
        '</md-input-container>' +
        '</form>' +
        '</md-edit-dialog>';

      $mdEditDialog.show({
        controller: ['$scope', '$element', function($scope, $element) {
          $scope.$watch('model', function ($newValue, $oldValue) {
            if (!angular.isUndefined($newValue) && $newValue !== $oldValue) {
              var bak = rowData.education;
              rowData.education = $scope.model;
              $http.put('/playground/grid/basic/data', rowData).success(function(data) {
                if (angular.isUndefined(data.errMsg)) {
                  $element.remove();
                }
                else {
                  rowData.education = bak;
                  $mdToast.show($mdToast.simple().textContent(data.errMsg));
                }
              });
            }
          });
        }],
        targetEvent: event,
        scope: {
          model: rowData.education,
          educations: educations
        },
        template: template
      });
    };

    var remarkEdit = function(rowData, field, event) {
      event.stopPropagation();

      var template = '<md-edit-dialog>' +
        '<form name="form2" class="md-content" ng-submit="submit()">' +
        '<md-input-container md-no-float class="md-block">' +
        '<input md-autofocus ng-model="model" md-maxlength="30" placeholder="备注">' +
        '</md-input-container>' +
        '</form>' +
        '</md-edit-dialog>';

      var isSubmit = true;
      $mdEditDialog.show({
        controller: ['$scope', '$element', function($scope, $element) {
          $scope.submit = function() {
            if ($scope.form2.$valid && isSubmit) {
              isSubmit = false;
              var bak = rowData.remark;
              rowData.remark = $scope.model;
              $http.put('/playground/grid/basic/data', rowData).success(function(data) {
                if (angular.isUndefined(data.errMsg)) {
                  $element.remove();
                }
                else {
                  isSubmit = true;
                  rowData.remark = bak;
                  $mdToast.show($mdToast.simple().textContent(data.errMsg));
                }
              });
            }
          };
        }],
        targetEvent: event,
        scope: {
          model: rowData.remark
        },
        template: template
      });
    };

    $scope.gridParam = {
      columns: [
        {field:'name', title: '姓名', width: 100},
        {field:'age', title: '年龄', width: 80, isNumeric: true},
        {field:'dept', title: '科室', width: 240},
        {field:'job', title: '职务', width: 200},
        {field:'title', title: '职称', width: 200},
        {field:'education', title: '学历', width: 200, edit: educationEdit, editComment: '点击新增学历'},
        {field:'remark', title: '备注', width: 260, edit: remarkEdit, editComment: '点击新增备注'}
      ],
      url: '/playground/grid/basic',
      title: '职工信息'
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('main.playgroundGridColumnEdit', {
      url: '^/playground/grid/columnEdit',
      templateUrl: 'modules/playground/grid/basic.html',
      controller: gridColumnEditCtrl
    });
  };
  app.config(mainRouter);
})(angular.module('pea'));
