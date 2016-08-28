(function(app) {
    'use strict';


    var forgetPasswordSettingCtrl = function($scope,$stateParams,$state,$ionicHistory,$http,$cordovaToast) {

        $scope.input = {
            code:'',
            password:''
        };
        //是否重新发送的标志位
        $scope.isResend = false;


        $scope.phone = $stateParams.phone.toString().substring(0,3)+'****'+$stateParams.phone.toString().substring(7,11);


        $scope.back = function(){
            $ionicHistory.goBack();
        };


        $scope.changePassword = function () {
            if($scope.input.password.length<6){
                alert('密码长度过短请重新设置');
            }else{
                var password = {
                    category:'2',
                    verificationCode:$scope.input.code,
                    password:$scope.input.password
                };
                $http.put('/permission/account', password).success(function(data) {
                    $state.go('tab.personal');
                    console.log(data);
                }).error(function(data){
                    $cordovaToast.showShortBottom(data);
                });
            }
        };

        $scope.resend = function () {

            var phone = {
                category:'3',
                phone:$stateParams.phone.toString()
            };

            $http.put('/permission/verificationCode', phone).success(function(data) {
                console.log(data);
            }).error(function(data){
                $cordovaToast.showShortBottom(data);
            });
        };

        $scope.time = {
            second:5
        };
        $scope.secondString = '('+$scope.time.second+')';
        var updateTime = function() {
            //倒计时结束可以重新发送验证码
            if ($scope.time.second === 0) {
                $scope.secondString = '';
            }else if($scope.time.second === 1){
                $scope.isResend = true;
                --$scope.time.second;
                $scope.secondString = '';
            }else{
                --$scope.time.second;
                $scope.secondString = '('+$scope.time.second+')';
            }
        };
        setInterval(function(){
            $scope.$apply(updateTime);

        }, 1000);
    };

    var mainRouter = function($stateProvider) {
        $stateProvider.state('forgetPasswordSetting', {
            url: '/forgetPasswordSetting',
            cache:'false',
            params:{'phone':''},
            templateUrl: 'modules/login/forgetPasswordSetting.html',
            controller: forgetPasswordSettingCtrl
        });
    };

    app.config(mainRouter);
})(angular.module('isj'));
