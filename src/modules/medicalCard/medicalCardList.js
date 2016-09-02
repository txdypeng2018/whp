(function(app) {
  'use strict';

  var medicalCardListCtrl = function($scope, $http, $state, $cordovaToast) {
    //家庭关系类别
    $http.get('/dataBase/familyMenberTypes').success(function(data) {
      $scope.memberTypes = data;
    }).error(function(data){
      $cordovaToast.showShortBottom(data);
    });

    $scope.$on('$ionicView.beforeEnter', function(){
      //取得家庭成员列表
      $http.get('/user/familyMembers').success(function(data) {
        $scope.members = data;
        for (var i = 0 ; i < $scope.members.length ; i++) {
          $scope.members[i].idCard = $scope.members[i].idCard.substring(0,6)+'********'+$scope.members[i].idCard.substring(14,18);
        }
      }).error(function(data){
        $cordovaToast.showShortBottom(data);
      });
    });

    //家庭成员选择事件
    $scope.medicalCardEdit = function(id) {
      $state.go('medicalCardEdit', {memberId: id});
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('medicalCardList', {
      url: '/medicalCard/medicalCardList',
      templateUrl: 'modules/medicalCard/medicalCardList.html',
      controller: medicalCardListCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
