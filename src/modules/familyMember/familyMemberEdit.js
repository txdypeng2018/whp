(function(app) {
  'use strict';

  var familyMemberEditCtrl = function($scope, $http, $stateParams, $cordovaToast) {
    $http.get('/dataBase/sexTypes').success(function(data) {
      $scope.sexTypes = data;
      $scope.sexs = [];
      for (var i in $scope.sexTypes) {
        $scope.sexTypes[i].code = i;
        $scope.sexs.push($scope.sexTypes[i]);
      }
    }).error(function(data){
      $cordovaToast.showShortBottom(data);
    });

    $scope.$on('$ionicView.beforeEnter', function(){
      $scope.isSubmit = false;
      $scope.hasSex = false;
      if (angular.isUndefined($stateParams.memberId) || $stateParams.memberId === '') {
        $scope.isAdd = true;
        $http.get('/dataBase/familyMenberTypes/'+$stateParams.memberCode).success(function(data) {
          $scope.member = {
            memberCode: data.code,
            member: data.name,
            sexCode: data.sexCode,
            sex: '',
            name: '',
            img: data.img
          };
          if (data.sexCode !== '') {
            $scope.member.sex = $scope.sexTypes[data.sexCode].name;
            $scope.hasSex = true;
          }
        }).error(function(data){
          $cordovaToast.showShortBottom(data);
        });
      }
      else {
        $scope.isAdd = false;
      }
    });
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('familyMemberEdit', {
      url: '/familyMember/familyMemberEdit/:memberId/:memberCode',
      templateUrl: 'modules/familyMember/familyMemberEdit.html',
      controller: familyMemberEditCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
