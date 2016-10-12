(function(app) {
  'use strict';

  var subjectSelectCtrl = function($scope, $rootScope, $http, $state, $stateParams, $timeout, $cordovaToast, $ionicPopup) {
    $scope.hideSearch = true;
    $scope.type = $stateParams.type;

    $scope.subjectId = '';

    $scope.$on('$ionicView.beforeEnter', function(){
      if($scope.type === '1' || $scope.type === '2'){
        //取得挂号须知
        $http.get('/register/agreement').success(function(data) {
          if (angular.isUndefined($rootScope.agreement) || $rootScope.agreement === '' || $rootScope.agreement !== data) {
            $rootScope.agreement = data;
            $scope.showAgreement();
          }
        }).error(function(data){
          $cordovaToast.showShortBottom(data);
        });
      }
    });
    $scope.$on('$ionicView.beforeLeave', function(){
      if (myPopup !== null) {
        myPopup.close();
      }
    });

    //取得学科列表
    var getSubjects = function() {
      $scope.subjects = [];
      $scope.subjectRights = '';
      $http.get('/subjects', {params: {districtId: $scope.districtId, type: $scope.type}}).success(function(data) {
        $scope.subjects = data;

        //默认选中第一个一级学科
        $scope.subjectId = data[0].id;
        if (angular.isUndefined(data[0].subjects) || data[0].subjects.length === 0) {
          $scope.subjectRights = [data[0]];
        }
        else {
          $scope.subjectRights = data[0].subjects;
        }
      }).error(function(data){
        $cordovaToast.showShortBottom(data);
      });
    };

    //取得院区信息
    $http.get('/organization/districts').success(function(data) {
      $scope.districts = [];
      for (var i = 0 ; i < data.length ; i++) {
        $scope.districts.push(data[i]);
      }
      //默认选中南湖院区
      $scope.districtId = data[0].id;
      getSubjects();
    }).error(function(data){
      $cordovaToast.showShortBottom(data);
    });

    //查询框显示隐藏事件
    $scope.searchClk = function() {
      $scope.hideSearch = !$scope.hideSearch;
      if (!$scope.hideSearch) {
        $timeout(function(){
          document.getElementById('subjectSelect_search_'+$scope.type).focus();
        }, 50);
      }
    };

    //查询事件
    $scope.doSearch = function() {
      if (!angular.isUndefined($scope.major) && $scope.major !== '') {
        if ($stateParams.type === '1') {
          $state.go('registerTodayDoctorList', {districtId: $scope.districtId, major: $scope.major});
          $scope.type = '0';
        }
        else if ($stateParams.type === '2') {
          $state.go('registerDoctorDateSelect', {districtId: $scope.districtId, major: $scope.major});
          $scope.type = '0';
        }
      }
    };

    //院区选择事件
    $scope.districtClk = function(id) {
      if ($scope.districtId !== id) {
        $scope.districtId = id;
        getSubjects();
      }
    };

    //左侧一级学科选择事件
    $scope.subjectLeftClk = function(id) {
      if ($scope.subjectId !== id) {
        $scope.subjectId = id;

        for (var i = 0 ; i < $scope.subjects.length ; i++) {
          if ($scope.subjects[i].id === id) {
            if (angular.isUndefined($scope.subjects[i].subjects) || $scope.subjects[i].subjects.length === 0) {
              $scope.subjectRights = [$scope.subjects[i]];
            }
            else {
              $scope.subjectRights = $scope.subjects[i].subjects;
            }
            break;
          }
        }
      }
    };

    var myPopup = null;
    $scope.showAgreement = function() {
      myPopup = $ionicPopup.show({
        template: '<div style="padding: 3px;font-size:15px">'+$rootScope.agreement+'</div>',
        title: '挂号须知',
        cssClass: 'agreement-popup',
        buttons: [
          {
            text: '我知道了',
            type: 'button-positive',
            onTap: function(e) {
              e.preventDefault();
              myPopup.close();
            }
          }
        ]
      });
    };

    //右侧一级科室选择事件
    $scope.subjectRightClk = function(id) {
      if ($stateParams.type === '1') {
        $state.go('registerTodayDoctorList', {districtId: $scope.districtId, subjectId: id});
          $scope.type = '0';
      }
      else if ($stateParams.type === '2') {
        $state.go('registerDoctorDateSelect', {districtId: $scope.districtId, subjectId: id});
          $scope.type = '0';
      }
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('subjectSelect', {
      url: '/subject/subjectSelect/:type',
      templateUrl: 'modules/subject/subjectSelect.html',
      controller: subjectSelectCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
