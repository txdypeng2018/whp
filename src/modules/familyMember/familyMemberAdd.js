(function(app) {
  'use strict';

  var familyMemberAddCtrl = function($scope, $state, $http, $cordovaToast) {
    //家庭关系类别
    $http.get('/dataBase/familyMenberTypes').success(function(data) {
      $scope.memberTypes = [];
      for (var i in data) {
        data[i].code = i;
        $scope.memberTypes.push(data[i]);
      }
      $scope.memberTypes.sort(function(a,b){return a.code - b.code;});
    }).error(function(data){
      $cordovaToast.showShortBottom(data);
    });

    //家庭成员选择事件
    $scope.medicalCardEdit = function(code) {
      $state.go('familyMemberEdit', {memberCode: code});
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('familyMemberAdd', {
      url: '/familyMember/familyMemberAdd',
      templateUrl: 'modules/familyMember/familyMemberAdd.html',
      controller: familyMemberAddCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
