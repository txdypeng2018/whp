/**
 * Created by Administrator on 2017/2/20.
 */
/**
 * Created by Administrator on 2017/1/9.
 */
(function(app) {
    'use strict';

    var customerInfoCtrl = function($scope, $http, $state, $stateParams, $mdDialog, $filter) {
        var userId = $stateParams.transData.userId;
        var accountUserId = $stateParams.transData.accountUserId;

        var returnUserInfo = function () {
            $state.go('main.customerQuery',{'transFromInfo':$stateParams.transData});
        };
        var showRegistrations = function(rowDatas) {
            $mdDialog.show({
                controller: function($scope) {
                  $scope.registrationsData = rowDatas[0];
                  $scope.registrationsData.createTime1 = $filter('subTime')($scope.registrationsData.createTime);
                  $scope.registrationsData.amount1 = $filter('addComma')($scope.registrationsData.amount);
                  $scope.registrationsData.registrationOrderReq.payTotalFee1 = $filter('addComma')($scope.registrationsData.registrationOrderReq.payTotalFee);
                  $scope.registrationsData.registrationRefundReq.refundFee1 = $filter('addComma')($scope.registrationsData.registrationRefundReq.refundFee);

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
                  $scope.orderNum = rowDatas[0].recipePaidDetailList[0].orderNum;
                  $scope.recipeordersData = rowDatas[0];

                  $scope.recipeordersData.createTime1 = $filter('subTime')($scope.recipeordersData.createTime);
                  $scope.recipeordersData.recipeNonPaidDetail.amount1 = $filter('addComma')($scope.recipeordersData.recipeNonPaidDetail.amount);
                  $scope.recipeordersData.recipeOrderReqMap[$scope.orderNum].payTotalFee1 = $filter('addComma')($scope.recipeordersData.recipeOrderReqMap[$scope.orderNum].payTotalFee);
                  $scope.recipeordersData.recipePaidFailDetailList.amount1 = $filter('addComma')($scope.recipeordersData.recipePaidFailDetailList.amount);
                  for(var i = 0;i < $scope.recipeordersData.recipePaidDetailList[0].detailList.length;i++){
                    $scope.recipeordersData.recipePaidDetailList[0].detailList[i].unitPrice1 = $filter('addComma')($scope.recipeordersData.recipePaidDetailList[0].detailList[i].unitPrice);
                    $scope.recipeordersData.recipePaidDetailList[0].detailList[i].ownCost1 = $filter('addComma')($scope.recipeordersData.recipePaidDetailList[0].detailList[i].ownCost);
                    $scope.recipeordersData.recipePaidDetailList[0].detailList[i].number = $scope.recipeordersData.recipePaidDetailList[0].detailList[i].qty/$scope.recipeordersData.recipePaidDetailList[0].detailList[i].packQty;
                  }

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

        var dbRegistrations = function (rowData) {
            var rowDatas = [rowData];
            showRegistrations(rowDatas);
        };

        var dbRecipeorders = function (rowData) {
            var rowDatas = [rowData];
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
                {field:'createTime', title: '挂号单创建时间',width:210,formatter: function(value){
                  return value.substring(0,19);
                }}
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
                {field:'createTime', title: '门诊流水号创建时间',width:210,formatter: function(value){
                    return value.substring(0,19);
                }}
            ],
            operations: [
                {text: '详细信息', click: showRecipeorders, iconClass: 'description_black', display: ['singleSelected']},
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
                {text: '详细信息', click: showMessages, iconClass: 'description_black', display: ['singleSelected']},
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
    app.filter('addComma',function(){
      return function(str){
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
    });
    app.filter('subTime',function () {
      return function(str){
          return str.substring(0,19);
      };
    });
})(angular.module('pea'));
