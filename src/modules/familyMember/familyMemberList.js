(function(app) {
  'use strict';

  var familyMemberListCtrl = function($scope, $http, $state) {
    //家庭关系类别
    $scope.relationshipTypes = {
      '00': {
        'name': '我',
        'img': './assets/images/pic_man.png'
      },
      '01': {
        'name': '爸爸',
        'img': './assets/images/pic_old_man.png'
      },
      '02': {
        'name': '妈妈',
        'img': './assets/images/pic_old_woman.png'
      },
      '03': {
        'name': '配偶',
        'img': './assets/images/pic_woman.png'
      }
    };

    //取得家庭成员列表
    $http.get('/familyMembers').success(function(data) {
      $scope.members = data;
      for (var i = 0 ; i < $scope.members.length ; i++) {
        $scope.members[i].idCard = $scope.members[i].idCard.substring(0,6)+'********'+$scope.members[i].idCard.substring(14,18);
        $scope.members[i].phone = $scope.members[i].phone.substring(0,3)+'****'+$scope.members[i].phone.substring(7,11);
      }
    });

    //添加事件
    $scope.familyMemberAdd = function() {
      $state.go('familyMemberSelect');
    };

    //家庭成员选择事件
    $scope.medicalCardEdit = function() {
      $state.go('familyMemberEdit');
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
