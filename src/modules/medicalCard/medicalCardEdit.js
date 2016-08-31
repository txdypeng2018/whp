(function(app) {
  'use strict';

  var medicalCardEditCtrl = function($scope, $http, $stateParams, $ionicHistory) {
    $scope.$on('$ionicView.beforeEnter', function(){
      $scope.isSubmit = false;
      $scope.input = {
        memberId: $stateParams.memberId,
        medicalNum: ''
      };

      $http.get('/user/familyMembers/familyMember', {params: {memberId: $stateParams.memberId}}).success(function(data) {
        $scope.input.medicalNum = data.medicalNum;
      }).error(function(data){
        $cordovaToast.showShortBottom(data);
      });
    });

    //更新病历号
    $scope.changeNum = function() {
      $scope.isSubmit = true;
      $http.put('/user/medicalNum', $scope.input).success(function(data) {
        $ionicHistory.goBack();
      }).error(function(data){
        $scope.isSubmit = false;
        $cordovaToast.showShortBottom(data);
      });
    };

    //创建病历号
    $scope.createNum = function() {
      $scope.isSubmit = true;
      $http.post('/user/medicalNum', $scope.input).success(function(data) {
        $ionicHistory.goBack();
      }).error(function(data){
        $scope.isSubmit = false;
        $cordovaToast.showShortBottom(data);
      });
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('medicalCardEdit', {
      url: '/medicalCard/medicalCardEdit/:memberId',
      templateUrl: 'modules/medicalCard/medicalCardEdit.html',
      controller: medicalCardEditCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
