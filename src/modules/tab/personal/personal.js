(function(app) {
  'use strict';

  var tabPersonalCtrl = function($scope, $http, $state) {
    //取得患者信息
    $http.get('/patients/patient').success(function(data) {
      $scope.patient = data;
      if ($scope.patient.phone !== null && $scope.patient.phone !== '') {
        $scope.patient.phone = $scope.patient.phone.substring(0,3)+'****'+$scope.patient.phone.substring(7,11);
      }
    });

    //取得收藏医生数量
    $http.get('/collection/doctors/count').success(function(data) {
      $scope.doctorCount = data;
    });

    //路由跳转
    $scope.itemRouter = function(routerId) {
      $state.go(routerId);
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('tab.personal', {
      url: '/personal',
      views:{
        'tab-personal':{
          templateUrl: 'modules/tab/personal/personal.html',
          controller: tabPersonalCtrl
        }
      }
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
