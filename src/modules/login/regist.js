(function(app) {
    'use strict';

    var registCtrl = function($scope,$state,$ionicHistory,$http,$cordovaToast) {

       $scope.input = {
           phone: ''
       };

        $scope.back = function(){
            $ionicHistory.goBack();
        };

        $scope.getCode = function(){
            var phone = {
                category:'1',
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
        $stateProvider.state('regist', {
            url: '/regist',
            cache:'false',
            templateUrl: 'modules/login/regist.html',
            controller: registCtrl
        });
    };

    app.config(mainRouter);
})(angular.module('isj'));


