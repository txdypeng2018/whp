(function(app) {
  'use strict';

  var changePasswordCtrl = function($scope,$state,$http,$ionicHistory) {
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
                  nowPwd: $scope.input.nowPwd,
                  newPwd: $scope.input.newPwd,
                  renewPwd:$scope.input.renewPwd
              };
              $http.put('/setting/changePassword', threePwd).success(function(data) {
                  if (angular.isUndefined(data.errMsg)) {
                     /* if(data.status === 'success'){

                      }else{

                      }*/
                      $state.go('settingIndex');
                  }
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
