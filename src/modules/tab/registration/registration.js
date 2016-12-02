(function(app) {
  'use strict';

  var tabRegistrationCtrl = function($scope, $state, $stateParams, $http, toastService, $ionicHistory) {
    //取得挂号单
    var registrationList = function() {
      $scope.registrations = null;
      $http.get('/register/registrations', {params: {viewTypeId: $scope.viewTypeId, memberId: $stateParams.memberId, index: $scope.httpIndex.index}}).success(function(data, status, headers, config) {
        if (angular.isUndefined($scope.httpIndex[config.params.index])) {
          $scope.registrations = data;
          diffRegistrations[$scope.viewTypeId] = data;
          for (var i = 0 ; i < $scope.registrations.length ; i++) {
            if ($scope.registrations[i].district.length > 2) {
              $scope.registrations[i].district = $scope.registrations[i].district.substring(0,2);
            }
          }
        }
      }).error(function(data, status, fun, config){
        if (angular.isUndefined($scope.httpIndex[config.params.index])) {
          $scope.registrations = [];
          toastService.show(data);
        }
      }).finally(function() {
        $scope.$broadcast('scroll.refreshComplete');
      });
    };

    var diffRegistrations = {};
    $scope.$on('$ionicView.beforeEnter', function(){
      diffRegistrations = {};
      $scope.patient = {};
      //取得就诊人
      $http.get('/user/familyMembers/familyMember', {params: {memberId: $stateParams.memberId}}).success(function(data) {
        $scope.patient = data;
      }).error(function(data){
        toastService.show(data);
      });
      //家庭关系类别
      $http.get('/dataBase/familyMenberTypes').success(function(data) {
        $scope.memberTypes = data;
      }).error(function(data){
        $scope.memberTypes = {};
        toastService.show(data);
      });

      getViewTypes();

      $scope.httpIndex = {index:1};

      $ionicHistory.clearHistory();
    });

    //选择家庭成员
    $scope.selectMember = function() {
      $state.go('familyMemberSelect', {skipId: 'tab.registration', memberId: $scope.patient.id});
    };

    //挂号单选择
    $scope.registrationClk = function(id) {
      $state.go('registrationView', {registrationId: id, memberId: $scope.patient.id});
    };

    //遮蔽罩取消
    $scope.spinnerCancel = function() {
      $scope.httpIndex[$scope.httpIndex.index] = 'CANCEL';
      $scope.$broadcast('scroll.refreshComplete');
    };

    //下拉刷新
    $scope.doRefresh = function() {
      $scope.httpIndex.index++;
      registrationList();
    };

    //取得挂号单分类信息
    var getViewTypes = function() {
      $http.get('/register/viewTypes').success(function(data) {
        $scope.viewTypes = data;
        for (var i = 0 ; i < data.length ; i++) {
          if(data[i].default === '1'){
            //默认选中进行中
            $scope.viewTypeId = data[i].id;
          }
        }
        registrationList();
      }).error(function(data){
        toastService.show(data);
      });
    };

    //分类跳转
    $scope.registrationBtnClk=function(id){
      if ($scope.viewTypeId !== id) {
        $scope.viewTypeId = id;
        $scope.httpIndex.index++;

        if(!angular.isUndefined(diffRegistrations[id])){
          $scope.registrations=diffRegistrations[id];
        }
        else{
          registrationList();
        }
      }
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('tab.registration', {
      url: '/registration/:memberId',
      views:{
        'tab-registration':{
          templateUrl: 'modules/tab/registration/registration.html',
          controller: tabRegistrationCtrl
        }
      }
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
