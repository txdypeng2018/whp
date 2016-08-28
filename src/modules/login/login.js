(function(app) {
    'use strict';

    var loginCtrl = function($scope, $http,$state,$ionicHistory,$cordovaToast) {
        $scope.input = {
            phone: '',
            password:''
        };

        $scope.back = function(){

            $state.go('tab.personal');
            $ionicHistory.goBack();
        };
        $scope.login = function(){
            var phoneAndPwd = {
                phone:$scope.input.phone.toString(),
                password:$scope.input.password
            };
            $http.post('/permission/login', phoneAndPwd).success(function(data) {
                    $state.go('tab.personal');
                console.log(data);
            }).error(function(data){
                $cordovaToast.showShortBottom(data);
            });
        };
    };

    var mainRouter = function($stateProvider) {
        $stateProvider.state('login', {
            url: '/login',
            cache:'false',
            templateUrl: 'modules/login/login.html',
            controller: loginCtrl
        });
    };

    app.config(mainRouter);
})(angular.module('isj'));

