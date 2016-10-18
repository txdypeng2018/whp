(function(app) {
  'use strict';

  var familyMemberListCtrl = function($scope, $http, $state, toastService) {
    $scope.$on('$ionicView.beforeEnter', function(){
      $scope.canAdd = false;
      $scope.members = null;
      //家庭关系类别
      $http.get('/dataBase/familyMenberTypes').success(function(data) {
        $scope.memberTypes = data;
      }).error(function(data){
        toastService.show(data);
      });
      //取得家庭成员列表
      $http.get('/user/familyMembers').success(function(data) {
        $scope.members = data;
        if ((9-$scope.members.length) > 0) {
          $scope.canAdd = true;
        }
        else {
          $scope.canAdd = false;
        }
        for (var i = 0 ; i < $scope.members.length ; i++) {
          $scope.members[i].idCard = $scope.members[i].idCard.substring(0,6)+'********'+$scope.members[i].idCard.substring(14,18);
          $scope.members[i].phone = $scope.members[i].phone.substring(0,3)+'****'+$scope.members[i].phone.substring(7,11);
        }
      }).error(function(data){
        $scope.members = [];
        toastService.show(data);
      });
    });

    //添加事件
    $scope.familyMemberAdd = function() {
      $state.go('familyMemberAdd');
    };

    //家庭成员选择事件
    $scope.medicalCardEdit = function(id) {
      $state.go('familyMemberEdit', {memberId: id});
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('familyMemberList', {
      url: '/familyMember/familyMemberList',
      templateUrl: 'modules/familyMember/familyMemberList.html',
      controller: familyMemberListCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
