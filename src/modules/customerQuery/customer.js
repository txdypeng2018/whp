/**
 * Created by Administrator on 2017/1/9.
 */
(function(app) {
    'use strict';

    var customerCtrl = function($scope, $http, $mdDialog, $mdToast) {
        $scope.cusStatus = [];
        $http.get('/dataBase/customerTypes').success(function (data) {
            for (var i = 0; i < data.length; i++) {
                $scope.cusStatus.push(data[i]);
            }
        }).error(function (data) {
            $mdToast.show($mdToast.simple().textContent(data));
        });

        //查询
        $scope.customerSearch = function(){
            var myQueryArray = [];
            var nameFlag = false;
            var telFlag = false;
            if(!angular.isUndefined($scope.filterName) && $scope.filterName !== ''){
                myQueryArray.push('"name" : "'+ $scope.filterName + '"');
                nameFlag = true;
            }
            if(!angular.isUndefined($scope.filterTel) && $scope.filterTel !== ''){
                myQueryArray.push('"phone" : "'+ $scope.filterTel + '"');
                telFlag = true;
            }
            if(!angular.isUndefined($scope.filterIdcard) && $scope.filterIdcard !== ''){
                myQueryArray.push('"idCard" :"'+ $scope.filterIdcard + '"');
            }
            var queryStr = myQueryArray.join(',');

            if(angular.isUndefined(queryStr) || queryStr === '' || !(nameFlag&&telFlag)){
                $mdToast.show('用户名，电话必须填');
            }else{
                if($scope.selectedStatusCode === '0'){
                    $scope.userInfo = true;
                    $scope.messageInfo = false;
                    $scope.registrationInfo = false;
                    $scope.registrationRefundInfo = false;
                    $scope.recipeorderInfo = false;
                    var dataToJson = [];
                    $scope.customerDatasAll = [];
                    $http.get('/msc/user_info',{params: {query:'{' + queryStr + '}'}}).success(function(data) {
                        for(var n = 0; n < data.data.length; n++){
                            dataToJson.push(JSON.parse(data.data[n]));
                        }
                        for (var i = 0; i < dataToJson.length; i++) {
                            $scope.customerDatasAll.push(dataToJson[i]);
                        }
                    });
                }
                if($scope.selectedStatusCode === '1'){
                    $scope.userInfo = false;
                    $scope.messageInfo = false;
                    $scope.registrationInfo = true;
                    $scope.registrationRefundInfo = true;
                    $scope.recipeorderInfo = false;
                    var userId1 = '';
                    var dataToJson1 = [];
                    var regDataToJson = [];
                    var regRefundDataToJson = [];
                    $scope.customerRegDatasAll = [];
                    $scope.customerRegRefundDatasAll = [];
                    $http.get('/msc/user_info',{params: {query:'{' + queryStr + '}'}}).success(function(data) {
                        for(var n = 0; n < data.data.length; n++){
                            dataToJson1.push(JSON.parse(data.data[n]));
                        }
                        userId1 = dataToJson1[0].userId;
                        $http.get('/msc/registration',{params: {query:'{ "patientId": "' + userId1 + '"}'}}).success(function(data) {
                            for(var n = 0; n < data.data.length; n++){
                                regDataToJson.push(JSON.parse(data.data[n]));
                            }
                            for (var i = 0; i < regDataToJson.length; i++) {
                                $scope.customerRegDatasAll.push(regDataToJson[i]);
                            }
                        });
                        $http.get('/msc/registration_refund_log',{params: {query:'{ "createUserId": "' + userId1 + '"}'}}).success(function(data) {
                            for(var n = 0; n < data.data.length; n++){
                                regRefundDataToJson.push(JSON.parse(data.data[n]));
                            }
                            for (var i = 0; i < regRefundDataToJson.length; i++) {
                                $scope.customerRegRefundDatasAll.push(regRefundDataToJson[i]);
                            }
                        });
                    });
                }
                if($scope.selectedStatusCode === '2'){
                    $scope.userInfo = false;
                    $scope.messageInfo = false;
                    $scope.registrationInfo = false;
                    $scope.registrationRefundInfo = false;
                    $scope.recipeorderInfo = true;
                    var userId2 = '';
                    var dataToJson2 = [];
                    var recipeDataToJson = [];
                    $scope.customerDatasAll = [];
                    $http.get('/msc/user_info',{params: {query:'{' + queryStr + '}'}}).success(function(data) {
                        for(var n = 0; n < data.data.length; n++){
                            dataToJson2.push(JSON.parse(data.data[n]));
                        }
                        userId2 = dataToJson2[0].userId;
                        $http.get('/msc/recipeorder',{params: {query:'{ "patientId": "' + userId2 + '"}'}}).success(function(data) {
                            for(var n = 0; n < data.data.length; n++){
                                recipeDataToJson.push(JSON.parse(data.data[n]));
                            }
                            for (var i = 0; i < recipeDataToJson.length; i++) {
                                $scope.customerDatasAll.push(recipeDataToJson[i]);
                            }
                        });
                    });
                }
                if($scope.selectedStatusCode === '3'){
                    $scope.userInfo = false;
                    $scope.messageInfo = true;
                    $scope.registrationInfo = false;
                    $scope.registrationRefundInfo = false;
                    $scope.recipeorderInfo = false;
                    var userId3 = '';
                    var dataToJson3 = [];
                    var msgDataToJson = [];
                    $scope.customerDatasAll = [];
                    $http.get('/msc/user_info',{params: {query:'{' + queryStr + '}'}}).success(function(data) {
                        for(var n = 0; n < data.data.length; n++){
                            dataToJson3.push(JSON.parse(data.data[n]));
                        }
                        userId3 = dataToJson3[0].userId;
                        $http.get('/msc/messages_info',{params: {query:'{ "userId": "' + userId3 + '"}'}}).success(function(data) {
                            for(var n = 0; n < data.data.length; n++){
                                msgDataToJson.push(JSON.parse(data.data[n]));
                            }
                            for (var i = 0; i < msgDataToJson.length; i++) {
                                $scope.customerDatasAll.push(msgDataToJson[i]);
                            }
                        });
                    });
                }
            }
        };
    };

    var mainRouter = function($stateProvider) {
        $stateProvider.state('main.customerQuery', {
            url: '^/customerQuery/customer',
            templateUrl: 'modules/customerQuery/customer.html',
            controller: customerCtrl
        });
    };
    app.config(mainRouter);
})(angular.module('pea'));
