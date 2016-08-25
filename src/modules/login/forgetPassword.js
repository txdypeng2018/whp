(function(app) {
    'use strict';

    var forgetPasswordCtrl = function($scope,$state,$ionicHistory,$http) {

        $scope.input = {
            phone: ''
        };

        $scope.back = function () {
          $ionicHistory.goBack();
        };

        $scope.getCode = function(){
            var phone = {
                phone:$scope.input.phone.toString()
            };
            $http.post('/login/forgetPassword', phone).success(function(data) {
                if (angular.isUndefined(data.errMsg)) {
                    if(data.status === 'success'){
                        $state.go('forgetPasswordSetting',{phone:$scope.input
                            .phone});
                    }else{
                        alert('手机号不存在！');
                    }
                }
            });

        };
    };

    var mainRouter = function($stateProvider) {
        $stateProvider.state('forgetPassword', {
            url: '/forgetPassword',
            cache:'false',
            templateUrl: 'modules/login/forgetPassword.html',
            controller: forgetPasswordCtrl
        });
    };

    app.config(mainRouter);
})(angular.module('isj'));


