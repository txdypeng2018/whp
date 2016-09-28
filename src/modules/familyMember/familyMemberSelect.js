(function(app) {
  'use strict';

  var familyMemberSelectCtrl = function($scope, $http, $state, $stateParams, $cordovaToast) {
    $scope.memberId = $stateParams.memberId;
    $scope.canAdd = false;

    $scope.$on('$ionicView.beforeEnter', function(){
      $scope.members = null;
      //取得家庭成员类别
      $http.get('/dataBase/familyMenberTypes').success(function(data) {
        $scope.memberTypes = data;
      }).error(function(data){
        $cordovaToast.showShortBottom(data);
      });
      //取得登录患者家庭成员
      $http.get('/user/familyMembers').success(function(data) {
        $scope.members = data;
        if ((9-$scope.members.length) > 0) {
          $scope.canAdd = true;
        }
      }).error(function(data){
        $scope.members = [];
        $cordovaToast.showShortBottom(data);
      });
    });

    //家庭成员管理
    $scope.memberManage = function() {
      $state.go('familyMemberList');
    };

    //家庭成员选择事件
    $scope.memberSelect = function(id) {
      if ($stateParams.skipId === 'registerConfirmToday') {
        $state.go($stateParams.skipId, {memberId: id, doctorId: $stateParams.doctorId, date: $stateParams.date});
      }
      else if ($stateParams.skipId === 'registerConfirmAppt') {
        $state.go($stateParams.skipId, {memberId: id, doctorId: $stateParams.doctorId, date: $stateParams.date});
      }
      else {
        $state.go($stateParams.skipId, {memberId: id});
      }
    };

    //添加家庭成员
    $scope.addMember = function() {
      $state.go('familyMemberAdd', {skipId: 'familyMemberSelect'});
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('familyMemberSelect', {
      url: '/familyMember/familyMemberSelect/:skipId/:memberId/:doctorId/:date',
      templateUrl: 'modules/familyMember/familyMemberSelect.html',
      controller: familyMemberSelectCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
