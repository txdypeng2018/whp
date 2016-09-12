(function(app) {
  'use strict';

  var registSettingCtrl = function($scope, $ionicHistory, $stateParams, $http, $cordovaToast, $state, userService) {
    $scope.$on('$ionicView.beforeEnter', function(){
      $scope.isResend = false;
      $scope.isSubmit = false;
      $scope.phone = $stateParams.phone.toString().substring(0,3)+'****'+$stateParams.phone.toString().substring(7,11);

      $scope.input = {
        phone: $stateParams.phone,
        verificationCode: '',
        password: '',
        name:'',
        idCard:''
      };

      //倒计时
      $scope.time = {
        second: 60
      };
      $scope.secondString = '('+$scope.time.second+')';
      var updateTime = function() {
        if ($scope.time.second !== 0) {
          --$scope.time.second;
          if ($scope.time.second === 0) {
            $scope.isResend = true;
            $scope.secondString = '';
          }
          else {
            $scope.secondString = '('+$scope.time.second+')';
          }
        }
      };

      setInterval(function(){
        $scope.$apply(updateTime);
      }, 1000);
    });

    //返回
    $scope.goBack = function(){
      $ionicHistory.goBack(-2);
    };

    //重发验证码
    $scope.resendCode = function() {
      var param = {
        category: '3',
        phone: $stateParams.phone
      };
      $http.get('/permission/verificationCode', {params: param}).success(function() {
        $scope.time.second = 60;
        $scope.secondString = '('+$scope.time.second+')';
        $scope.isResend = false;
      }).error(function(data){
        $cordovaToast.showShortBottom(data);
      });
    };

    //验证身份证号
    var identityCodeValid = function(code) {
        var pass = true;
        if(code.length !== 15 || code.length !== 18){
            pass = false;
        }
      return pass;
    };

    //页面验证
    var dataValidation = function() {
      if ($scope.input.verificationCode === '') {
        $cordovaToast.showShortBottom('验证码不能为空');
        return false;
      }
      if ($scope.input.password === '') {
        $cordovaToast.showShortBottom('密码不能为空');
        return false;
      }
      if ($scope.input.name === '') {
        $cordovaToast.showShortBottom('姓名不能为空');
        return false;
      }
      if ($scope.input.idCard === '') {
        $cordovaToast.showShortBottom('身份证号不能为空');
        return false;
      }
      if ($scope.input.verificationCode.toString().length !== 6) {
        $cordovaToast.showShortBottom('验证码必须6个数字');
        return false;
      }
      if ($scope.input.password.length < 6 || $scope.input.password.length > 20) {
        $cordovaToast.showShortBottom('密码必须6-20个字符');
        return false;
      }
      var reg = /^[0-9a-zA-Z_]+$/;
      if(!reg.test($scope.input.password)){
        $cordovaToast.showShortBottom('密码必须由英文字母、数字、下划线组成');
        return false;
      }
      if(!identityCodeValid($scope.input.idCard)){
        $cordovaToast.showShortBottom('身份证号输入错误');
        return false;
      }
      return true;
    };

    $scope.regist = function(){
      $scope.isSubmit = true;
      if (dataValidation()) {
        $http.post('/permission/account', $scope.input).success(function(data) {
          userService.setToken(data);
          $state.go('tab.personal');
          $cordovaToast.showShortBottom('注册成功');
        }).error(function(data){
          $scope.isSubmit = false;
          $cordovaToast.showShortBottom(data);
        });
      }
      else {
        $scope.isSubmit = false;
      }
    };
  };

    var mainRouter = function($stateProvider) {
      $stateProvider.state('registSetting', {
        url: '/registSetting/:phone',
        templateUrl: 'modules/login/registSetting.html',
        controller: registSettingCtrl
      });
    };

    app.config(mainRouter);
})(angular.module('isj'));


