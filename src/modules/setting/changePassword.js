(function(app) {
  'use strict';

  var changePasswordCtrl = function($scope,$state,$http,$ionicHistory,$cordovaToast) {
      $scope.input = {
          nowPwd: '',
          newPwd: '',
          renewPwd:''
      };

      $scope.back = function(){
          $ionicHistory.goBack();
      };

      $scope.change = function () {
          if($scope.input.newPwd === $scope.input.renewPwd){
              var threePwd = {
                  category:'1',
                  nowPwd: $scope.input.nowPwd,
                  newPwd: $scope.input.newPwd,
                  renewPwd:$scope.input.renewPwd
              };
              $http.put('/permission/account', threePwd).success(function(data) {
                  $state.go('tab.personal');
                  console.log(data);
              }).error(function(data){
                  $cordovaToast.showShortBottom(data);
              });
          }else{
              alert('两次密码输入不一致！');
          }
      };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('settingChangePassword', {
      url: '/setting/changePassword',
      templateUrl: 'modules/setting/changePassword.html',
      controller: changePasswordCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
