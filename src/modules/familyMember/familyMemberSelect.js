(function(app) {
  'use strict';

  var familyMemberSelectCtrl = function($scope, $http, $state, $stateParams) {
    $scope.memberId = $stateParams.memberId;

    //取得家庭成员类别
    $http.get('/dataBase/familyMenberTypes').success(function(data) {
      $scope.memberTypes = data;
    });

    //取得登录患者家庭成员
    $http.get('/familyMembers').success(function(data) {
      $scope.members = data;
    });

    //家庭成员管理
    $scope.memberManage = function() {
      $state.go('familyMemberList');
    };

    //家庭成员选择事件
    $scope.memberSelect = function(code, id) {
      if (code === '00') {
        id = '';
      }
      $state.go($stateParams.skipId, {memberId: id});
    };

    //添加家庭成员
    $scope.addMember = function() {
      $state.go('familyMemberAdd', {skipId: $stateParams.skipId, memberId: $stateParams.memberId, type: '2'});
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('familyMemberSelect', {
      url: '/familyMember/familyMemberSelect/:skipId/:memberId',
      cache: 'false',
      templateUrl: 'modules/familyMember/familyMemberSelect.html',
      controller: familyMemberSelectCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
