(function(app) {
    'use strict';


    var registSettingCtrl = function($scope,$ionicHistory,$stateParams,$http,$cordovaToast,$state) {
        $scope.isResend = false;
        $scope.input = {
            name:'',
            password:'',
            code:'',
            idCard:''
        };
        $scope.phone = $stateParams.phone.toString().substring(0,3)+'****'+$stateParams.phone.toString().substring(7,11);


        $scope.isResend = false;

        $scope.back = function(){
            $ionicHistory.goBack();
        };


        $scope.regist = function(){
            var regist = {
                phone:$stateParams.phone.toString(),
                idCard:$scope.input.idCard.toString(),
                verificationCode:$scope.input.code,
                name:$scope.input.name,
                password:$scope.input.password
            };
            $http.post('/permission/verificationCode', regist).success(function(data) {
                    console.log(data);
                    $state.go('tab.personal');
            }).error(function(data){
                $cordovaToast.showShortBottom(data);
            });
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
            //console.log($scope.time.second);
        }, 1000);
    };

    var mainRouter = function($stateProvider) {
        $stateProvider.state('registSetting', {
            url: '/registSetting',
            cache:'false',
            params:{'phone':''},
            templateUrl: 'modules/login/registSetting.html',
            controller: registSettingCtrl
        });
    };

    app.config(mainRouter);
})(angular.module('isj'));


