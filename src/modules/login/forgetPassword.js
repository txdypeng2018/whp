(function(app) {
    'use strict';

    var forgetPasswordCtrl = function($scope,$state,$ionicHistory,$http,$cordovaToast) {

        $scope.input = {
            phone: ''
        };

        $scope.back = function () {
          $ionicHistory.goBack();
        };

        $scope.getCode = function(){
            var phone = {
                category:'2',
                phone:$scope.input.phone.toString()
            };
            $http.get('/permission/verificationCode', phone).success(function(data) {
                console.log(data);
            }).error(function(data){
                $cordovaToast.showShortBottom(data);
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


