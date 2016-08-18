(function(app) {
    'use strict';

    var registCtrl = function($scope,$state) {
       $scope.input = {
           phone: ''
       };


        $scope.getCode = function(){
            $state.go('registSetting');
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


