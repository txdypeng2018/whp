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
            if($scope.registrations[i].itemName.length === 1){
              $scope.registrations[i].priceIsShow1 = false;
              $scope.registrations[i].priceIsShow2 = true;
            }else{
              $scope.registrations[i].priceIsShow1 = true;
              $scope.registrations[i].priceIsShow2 = false;
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
    $scope.viewTypeId = '1';
    $scope.viewTypes = [
      {
        'id': '1',
        'name': '未完成'
      },
      {
        'id': '2',
        'name': '已完成'
      }
    ];

    $scope.$on('$ionicView.beforeEnter', function(){
      for (var i in $ionicHistory.viewHistory().views) {
        var view = $ionicHistory.viewHistory().views[i];
        if (view.stateName === 'registerConfirmAppt' || view.stateName === 'paymentResult'){
          $scope.viewTypeId = '1';
        }
      }
      $scope.httpIndex = {index:1};
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

      registrationList();

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
