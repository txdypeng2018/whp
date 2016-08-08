(function(app) {
  'use strict';

  var hospitalNavigationCtrl = function($scope, $http, $timeout , $state, $ionicModal) {
    //取得院区楼列表
    var getBuildList = function() {
      var districtId = $scope.districtSelectedId;
      $http.get('/hospitalNavigation/builds', {params: {districtId:districtId}}).success(function(data) {
        $scope.buildList = data;
      });
    };

    //院区楼列表模型
    $ionicModal.fromTemplateUrl('modules/hospitalNavigation/buildingList.html', {
      scope: $scope,
      animation: 'slide-in-up'
    }).then(function(modal) {
      $scope.modal = modal;
    });

    //楼选中事件
    $scope.buildingClk = function(id) {
      $scope.modal.hide();
      $state.go('hospitalNavigationFloorList', {id: id});
    };

    //院区选择事件
    $scope.districtClk = function(event, id, name) {
      var districtDocuments = document.querySelectorAll('.district-button');
      for (var i = 0 ; i < districtDocuments.length ; i++) {
        if (!angular.element(districtDocuments[i]).hasClass('button-outline')) {
          angular.element(districtDocuments[i]).addClass('button-outline');
        }
      }
      angular.element(event.currentTarget).removeClass('button-outline');

      $scope.districtSelectedId = id;
      $scope.districtSelectedName = name;
      getBuildList();
    };

    //取得院区信息
    $http.get('/organization/district').success(function(data) {
      $scope.districts = data;

      //默认选中第一个院区
      $timeout(function(){
        if ($scope.districts.length > 0) {
          angular.element(document.querySelectorAll('.district-button')[0]).removeClass('button-outline');
          $scope.districtSelectedId = $scope.districts[0].id;
          $scope.districtSelectedName = $scope.districts[0].name;
          getBuildList();
        }
      });
    });
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('hospitalNavigation', {
      url: '/hospitalNavigation',
      templateUrl: 'modules/hospitalNavigation/hospitalNavigation.html',
      controller: hospitalNavigationCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
