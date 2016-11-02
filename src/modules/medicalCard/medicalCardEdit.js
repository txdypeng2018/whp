(function(app) {
  'use strict';

  var medicalCardEditCtrl = function($scope, $http, $stateParams, $ionicHistory, toastService) {
    $scope.$on('$ionicView.beforeEnter', function(){
      $scope.isSubmit = false;
      $scope.input = {
        memberId: $stateParams.memberId,
        medicalNum: ''
      };

      $http.get('/user/familyMembers/familyMember', {params: {memberId: $stateParams.memberId}}).success(function(data) {
        $scope.input.medicalNum = data.medicalNum;
      }).error(function(data){
        toastService.show(data);
      });
    });

    //更新病历号
    $scope.changeNum = function() {
      $scope.isSubmit = true;
      $http.put('/user/medicalNum', $scope.input).success(function() {
        $ionicHistory.goBack();
        toastService.show('更新成功');
      }).error(function(data){
        $scope.isSubmit = false;
        toastService.show(data);
      });
    };

    //创建病历号
    $scope.createNum = function() {
      $scope.isSubmit = true;
      $http.post('/user/medicalNum').success(function() {
        $ionicHistory.goBack();
        toastService.show('建档成功');
      }).error(function(data){
        $scope.isSubmit = false;
        toastService.show(data);
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
