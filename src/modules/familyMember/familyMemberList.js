(function(app) {
  'use strict';

  var familyMemberListCtrl = function($scope, $http, $state, toastService) {
    //取得家庭成员列表
    var getFamilyMembers = function() {
      $scope.members = null;
      $http.get('/user/familyMembers', {params: {index: $scope.httpIndex.index}}).success(function(data, status, headers, config) {
        if (angular.isUndefined($scope.httpIndex[config.params.index])) {
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
        }
      }).error(function(data, status, fun, config){
        if (angular.isUndefined($scope.httpIndex[config.params.index])) {
          $scope.members = [];
          toastService.show(data);
        }
      }).finally(function() {
        $scope.$broadcast('scroll.refreshComplete');
      });
    };

    $scope.$on('$ionicView.beforeEnter', function(){
      $scope.canAdd = false;
      //家庭关系类别
      $http.get('/dataBase/familyMenberTypes').success(function(data) {
        $scope.memberTypes = data;
      }).error(function(data){
        toastService.show(data);
      });

      $scope.httpIndex = {index:1};
      getFamilyMembers();
    });

    //添加事件
    $scope.familyMemberAdd = function() {
      $state.go('familyMemberAdd');
    };

    //家庭成员选择事件
    $scope.medicalCardEdit = function(id) {
      $state.go('familyMemberEdit', {memberId: id});
    };

    //遮蔽罩取消
    $scope.spinnerCancel = function() {
      $scope.httpIndex[$scope.httpIndex.index] = 'CANCEL';
      $scope.$broadcast('scroll.refreshComplete');
    };

    //下拉刷新
    $scope.doRefresh = function() {
      $scope.httpIndex.index++;
      getFamilyMembers();
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
