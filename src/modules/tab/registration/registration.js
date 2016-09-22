(function(app) {
  'use strict';

  var tabRegistrationCtrl = function($scope, $state, $stateParams, $http, $cordovaToast,$ionicHistory) {
    //取得挂号单
    var registrationList = function() {
      $http.get('/register/registrations', {params: {memberId: $stateParams.memberId}}).success(function(data) {
        $scope.registrations = data;
      }).error(function(data){
        $scope.registrations = [];
        $cordovaToast.showShortBottom(data);
      });
    };

    $scope.$on('$ionicView.beforeEnter', function(){
      $scope.patient = {};
      $scope.registrations = null;
      //取得就诊人
      $http.get('/user/familyMembers/familyMember', {params: {memberId: $stateParams.memberId}}).success(function(data) {
        $scope.patient = data;
      }).error(function(data){
        $cordovaToast.showShortBottom(data);
      });
      //家庭关系类别
      $http.get('/dataBase/familyMenberTypes').success(function(data) {
        $scope.memberTypes = data;
      }).error(function(data){
        $scope.memberTypes = {};
        $cordovaToast.showShortBottom(data);
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
