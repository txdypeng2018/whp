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
      code = code.toString();
      var city = {11:'北京',12:'天津',13:'河北',14:'山西',15:'内蒙古',21:'辽宁',22:'吉林',23:'黑龙江',31:'上海',32:'江苏',33:'浙江',34:'安徽',35:'福建',36:'江西',37:'山东',41:'河南',42:'湖北',43:'湖南',44:'广东',45:'广西',46:'海南',50:'重庆',51:'四川',52:'贵州',53:'云南',54:'西藏',61:'陕西',62:'甘肃',63:'青海',64:'宁夏',65:'新疆',71:'台湾',81:'香港',82:'澳门',91:'国外'};
      var tip = '';
      var pass = true;

      if(!code || !/^\d{6}(18|19|20)?\d{2}(0[1-9]|1[12])(0[1-9]|[12]\d|3[01])\d{3}(\d|X)$/i.test(code)){
        tip = '身份证号格式错误';
        pass = false;
      }

      else if(!city[code.substr(0,2)]){
        tip = '地址编码错误';
        pass = false;
      }
      else{
        //18位身份证需要验证最后一位校验位
        if(code.length === 18){
          code = code.split('');
          //∑(ai×Wi)(mod 11)
          //加权因子
          var factor = [ 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 ];
          //校验位
          var parity = [ 1, 0, 'X', 9, 8, 7, 6, 5, 4, 3, 2 ];
          var sum = 0;
          var ai = 0;
          var wi = 0;
          for (var i = 0; i < 17; i++)
          {
            ai = code[i];
            wi = factor[i];
            sum += ai * wi;
          }
          if(parity[sum % 11].toString() !== code[17]){
            tip = '校验位错误';
            pass = false;
          }
        }
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


