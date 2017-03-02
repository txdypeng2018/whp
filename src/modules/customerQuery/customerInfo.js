/**
 * Created by Administrator on 2017/2/20.
 */
/**
 * Created by Administrator on 2017/1/9.
 */
(function(app) {
    'use strict';

    var customerInfoCtrl = function($scope, $http, $state, $stateParams, $mdDialog) {
        var userId = $stateParams.transData.userId;
        var accountUserId = $stateParams.transData.accountUserId;
        var addComma = function(str){
            var strIntDoc= (str/100).toString();
            var strInt = strIntDoc.split('.')[0];
            var strDoc = strIntDoc.split('.')[1];
            if(angular.isUndefined(strDoc)){
                strDoc = new Array(strDoc,'00').join('');
            }else if(strDoc.length === 1) {
                strDoc = new Array(strDoc, '0').join('');
            }
            var str1=strInt.split('').reverse().join('');
            var str2='';
            for(var i = 0; i < str1.length; i++){
                if(i * 3 + 3 >=str1.length){
                    str2 += str1.substring(i*3, str1.length);
                    break;
                }
                str2 += str1.substring(i*3, i*3+3)+',';
            }
            return new Array(str2.split('').reverse().join(''),strDoc).join('.');
        };

        var returnUserInfo = function () {
            $state.go('main.customerQuery',{'transFromInfo':$stateParams.transData});
        };
        var showRegistrations = function(rowDatas) {
            $mdDialog.show({
                controller: function($scope) {
                    $scope.registrationsData = rowDatas[0];

                    $scope.cancel = function() {
                        $mdDialog.cancel();
                    };
                },
                templateUrl: 'modules/customerQuery/registrationsDialog.html',
                parent: angular.element(document.body),
                fullscreen: true
            });
        };

        var showRecipeorders = function(rowDatas) {
            $mdDialog.show({
                controller: function($scope) {
                    $scope.recipeordersData = rowDatas[0];

                    $scope.cancel = function() {
                        $mdDialog.cancel();
                    };
                },
                templateUrl: 'modules/customerQuery/recipeordersDialog.html',
                parent: angular.element(document.body),
                fullscreen: true
            });
        };

        var showMessages = function(rowDatas) {
            $mdDialog.show({
                controller: function($scope) {
                    $scope.messagesData = rowDatas[0];

                    $scope.cancel = function() {
                        $mdDialog.cancel();
                    };
                },
                templateUrl: 'modules/customerQuery/messagesDialog.html',
                parent: angular.element(document.body),
                fullscreen: true
            });
        };

        var dbRegistrationsFlag = 1;
        var dbRegistrationsFlag1 = 1;
        var dbRegistrationsFlag2 = 1;
        var dbRegistrationsFlag3 = 1;
        var dbRegistrationsFlag4 = 1;
        var dbRegistrations = function (rowData) {
            var rowDatas = [rowData];
            if(dbRegistrationsFlag === 1){
                if(dbRegistrationsFlag1 === 1 && rowDatas[0].statusCode === '0'){
                    rowDatas[0].amount = addComma(rowDatas[0].amount);
                    rowDatas[0].registrationOrderReq.payTotalFee = addComma(rowDatas[0].registrationOrderReq.payTotalFee);
                    dbRegistrationsFlag1 = 0;
                }
                if(dbRegistrationsFlag2 === 1 && rowDatas[0].statusCode === '1'){
                    rowDatas[0].amount = addComma(rowDatas[0].amount);
                    rowDatas[0].registrationOrderReq.payTotalFee = addComma(rowDatas[0].registrationOrderReq.payTotalFee);
                    dbRegistrationsFlag2 = 0;
                }
                if(dbRegistrationsFlag3 === 1 && rowDatas[0].statusCode === '6'){
                    rowDatas[0].amount = addComma(rowDatas[0].amount);
                    rowDatas[0].registrationOrderReq.payTotalFee = addComma(rowDatas[0].registrationOrderReq.payTotalFee);
                    rowDatas[0].registrationRefundReq.totalFee = addComma(rowDatas[0].registrationRefundReq.totalFee);
                    rowDatas[0].registrationRefundReq.refundFee = addComma(rowDatas[0].registrationRefundReq.refundFee);
                    dbRegistrationsFlag3 = 0;
                }
                if(dbRegistrationsFlag4 === 1 && rowDatas[0].statusCode === '8'){
                    rowDatas[0].amount = addComma(rowDatas[0].amount);
                    rowDatas[0].registrationOrderReq.payTotalFee = addComma(rowDatas[0].registrationOrderReq.payTotalFee);
                    dbRegistrationsFlag4 = 0;
                }
                if(dbRegistrationsFlag1 === 0 && dbRegistrationsFlag2 === 0 && dbRegistrationsFlag3 === 0 && dbRegistrationsFlag4 === 0){
                    dbRegistrationsFlag = 0;
                }
            }
            showRegistrations(rowDatas);
        };
        var dbRecipeordersFlag = 1;
        var dbRecipeordersFlag1 = 1;
        var dbRecipeordersFlag2 = 1;
        var dbRecipeordersFlag3 = 1;
        var dbRecipeorders = function (rowData) {
            var rowDatas = [rowData];
            if(dbRecipeordersFlag === 1){
                if(dbRecipeordersFlag1 === 1 && rowDatas[0].statusCode === '0'){
                    rowDatas[0].recipeNonPaidDetail.amount = addComma(rowDatas[0].recipeNonPaidDetail.amount);
                    dbRecipeordersFlag1 = 0;
                }
                if(dbRecipeordersFlag2 === 1 && rowDatas[0].statusCode === '1'){
                    rowDatas[0].recipeOrderReqMap.payTotalFee = addComma(rowDatas[0].recipeOrderReqMap.payTotalFee);
                    dbRecipeordersFlag2 = 0;
                }
                if(dbRecipeordersFlag3 === 1 && rowDatas[0].statusCode === '2'){
                    rowDatas[0].recipeOrderReqMap.payTotalFee = addComma(rowDatas[0].recipeOrderReqMap.payTotalFee);
                    rowDatas[0].recipePaidFailDetailList.amount = addComma(rowDatas[0].recipePaidFailDetailList.amount);
                    dbRecipeordersFlag3 = 0;
                }
                if(dbRecipeordersFlag1 === 0 && dbRecipeordersFlag2 === 0 && dbRecipeordersFlag3 === 0){
                    dbRecipeordersFlag = 0;
                }
            }
            showRecipeorders(rowDatas);
        };
        var dbMessages = function (rowData) {
            var rowDatas = [rowData];
            showMessages(rowDatas);
        };

        $scope.gridParam1 = {
            columns: [
                {field:'patientName', title: '患者姓名',width:100},
                {field:'patientCardNo', title: '患者病历号',width:150},
                {field:'num', title: '挂号单单号',width:170},
                {field:'orderNum', title: '交易单号',width:210},
                {field:'status', title: '交易状态',width:120},
                {field:'createTime', title: '挂号单创建时间',width:210}
            ],
            operations: [
                {text: '详细信息', click: showRegistrations, iconClass: 'description_black', display: ['singleSelected']},
                {text: '返回人员信息列表', click: returnUserInfo, iconClass: 'arrow_back_black', display: ['unSelected','singleSelected','multipleSelected']}
            ],
            title: '挂号单详细信息',
            url: '/admin/customer/registrations',
            searchData: {
                userId: userId
            },
            dblClick: dbRegistrations
        };
        $scope.gridParam2 = {
            columns: [
                {field:'patientName', title: '患者姓名',width:100},
                {field:'clinicCode', title: '门诊流水号',width:120},
                {field:'operatorName', title: '操作者姓名',width:100},
                {field:'operatorPhone', title: '操作者电话',width:150},
                {field:'status', title: '交易状态',width:120},
                {field:'createTime', title: '门诊流水号创建时间',width:210}
            ],
            operations: [
                {text: '详细信息', click: showRecipeorders, iconClass: 'search_black', display: ['singleSelected']},
                {text: '返回人员信息列表', click: returnUserInfo, iconClass: 'arrow_back_black', display: ['unSelected','singleSelected','multipleSelected']}
            ],
            title: '门诊流水号详细信息',
            url: '/admin/customer/recipeorders',
            searchData: {
                userId: userId
            },
            dblClick: dbRecipeorders
        };
        $scope.gridParam3 = {
            columns: [
                {field:'date', title: '推送时间',width:170},
                {field:'content', title: '推送信息',width:850}
            ],
            operations: [
                {text: '详细信息', click: showMessages, iconClass: 'search_black', display: ['singleSelected']},
                {text: '返回人员信息列表', click: returnUserInfo, iconClass: 'arrow_back_black', display: ['unSelected','singleSelected','multipleSelected']}
            ],
            title: '推送信息',
            url: '/admin/customer/messages',
            searchData: {
                userId: accountUserId
            },
            dblClick: dbMessages
        };
    };

    var mainRouter = function($stateProvider) {
        $stateProvider.state('main.customerInfo', {
            url: '^/customerQuery/customerInfo',
            params: {'transData': null},
            templateUrl: 'modules/customerQuery/customerInfo.html',
            controller: customerInfoCtrl
        });
    };
    app.config(mainRouter);
})(angular.module('pea'));
