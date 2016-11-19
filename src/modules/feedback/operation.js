(function(app) {
  'use strict';

  var gridOperationCtrl = function($scope, $http, $mdDialog, $mdToast) {
    //取得反馈状态列表
    $scope.opStatus = [];
    $scope.opStatus.push({
      code: '',
      name: '全部'
    });
    $http.get('/dataBase/feedbackTypes').success(function (data) {
      for (var i = 0; i < data.length; i++) {
        $scope.opStatus.push(data[i]);
      }
    }).error(function (data) {
      $mdToast.show($mdToast.simple().textContent(data));
    });

    var searchData = function() {
      $scope.gridParam.showSearchBar();
    };

    var editData = function(rowDatas) {
      $mdDialog.show({
        controller: function($scope) {
          $http.get('/service/userOpinion/' + rowDatas[0].id).success(function(data) {
            $scope.opinion = data;
          });

          $scope.isSubmit = false;

          $scope.cancel = function() {
            $mdDialog.cancel();
          };

          $scope.submit = function() {
            if ($scope.opinionForm.$valid) {
              $scope.isSubmit = true;
              $http.put('/service/feedbackOpinion', $scope.opinion).success(function(data) {
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
        templateUrl: 'modules/feedback/operationDialog.html',
        parent: angular.element(document.body),
        fullscreen: true
      }).then(function() {
        $scope.gridParam.loadData();
      });
    };


    $scope.gridParam = {
      columns: [
        {field:'userName', title: '姓名', width: 100},
        {field:'userTel', title: '手机号', width: 120},
        {field:'opinion', title: '用户意见', width: 700},
        {field:'statusCode', title: '反馈状态', width: 100, formatter: function(value){
          for(var i = 0; i < $scope.opStatus.length; i++) {
            if(value !== '' && $scope.opStatus[i].code === value) {
              return $scope.opStatus[i].name;
            }
          }
        }, styler: function(value) {
          if (value === '0') {
            return 'red';
          } else if (value === '1') {
            return 'green';
          }
        }}
      ],
      url: '/service/feedbackOpinion',
      operations: [
        {text: '查询', click: searchData, iconClass: 'search_black', display: ['unSelected']},
        {text: '编辑', click: editData, iconClass: 'edit_black', display: ['singleSelected']},
      ],
      title: '意见反馈信息',
      //multiSelect: true,
      isAllDataLoad: false,
      searchTemplate: 'modules/feedback/operationSearch.html',
      searchInitData: {
        statusCode: $scope.opStatus
      }
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('main.feedbackGridOperation', {
      url: '^/feedback/operation',
      templateUrl: 'modules/feedback/operation.html',
      controller: gridOperationCtrl
    });
  };
  app.config(mainRouter);
})(angular.module('pea'));
