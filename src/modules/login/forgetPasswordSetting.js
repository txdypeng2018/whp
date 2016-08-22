(function(app) {
    'use strict';

    var forgetPasswordSettingCtrl = function($scope,$ionicHistory) {

        $scope.isResend = false;

        $scope.back = function(){
            $ionicHistory.goBack();
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
            templateUrl: 'modules/login/forgetPasswordSetting.html',
            controller: forgetPasswordSettingCtrl
        });
    };

    app.config(mainRouter);
})(angular.module('isj'));


