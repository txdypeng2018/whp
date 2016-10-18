(function(app) {
  'use strict';
  var changePasswordCtrl = function($scope, $state, $http, $ionicHistory, toastService) {
    $scope.$on('$ionicView.beforeEnter', function(){
      $scope.isSubmit = false;
      $scope.input = {
        nowPassword: '',
        newPassword: '',
        confirmPassword: ''
      };
    });

    //页面验证
    var dataValidation = function() {
      if ($scope.input.nowPassword === '') {
        toastService.show('当前密码不能为空');
        return false;
      }
      if ($scope.input.newPassword === '') {
        toastService.show('新密码不能为空');
        return false;
      }
      if ($scope.input.confirmPassword === '') {
        toastService.show('确认新密码不能为空');
        return false;
      }
      if ($scope.input.newPassword !== $scope.input.confirmPassword) {
        toastService.show('新密码和确认新密码不一致');
        return false;
      }
      if ($scope.input.nowPassword.length < 6 || $scope.input.nowPassword.length > 20) {
        toastService.show('当前密码必须6-20个字符');
        return false;
      }
      if ($scope.input.newPassword.length < 6 || $scope.input.newPassword.length > 20) {
        toastService.show('新密码必须6-20个字符');
        return false;
      }
      if ($scope.input.confirmPassword.length < 6 || $scope.input.confirmPassword.length > 20) {
        toastService.show('确认新密码必须6-20个字符');
        return false;
      }
      var reg = /^[0-9a-zA-Z_]+$/;
      if(!reg.test($scope.input.nowPassword)){
        toastService.show('当前密码必须由英文字母、数字、下划线组成');
        return false;
      }
      if(!reg.test($scope.input.newPassword)){
        toastService.show('新密码必须由英文字母、数字、下划线组成');
        return false;
      }
      if(!reg.test($scope.input.confirmPassword)){
        toastService.show('确认新密码必须由英文字母、数字、下划线组成');
        return false;
      }
      return true;
    };

    //确认修改
    $scope.change = function() {
      $scope.isSubmit = true;
      if (dataValidation()) {
        $scope.input.category = '1';
        $http.put('/permission/account', $scope.input).success(function() {
          $ionicHistory.goBack();
          toastService.show('密码修改成功');
        }).error(function(data){
          $scope.isSubmit = false;
          toastService.show(data);
        });
      }
      else {
        $scope.isSubmit = false;
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
