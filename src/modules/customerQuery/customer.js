/**
 * Created by Administrator on 2017/1/9.
 */
(function(app) {
    'use strict';

    var customerCtrl = function($scope, $http, $state, $mdToast, $stateParams) {
        if($stateParams.transFromInfo !== null){
            var sParams = {
                name: $stateParams.transFromInfo.name,
                medicalNum: $stateParams.transFromInfo.medicalNum,
                phoneOridCard: $stateParams.transFromInfo.phone
            };
            $http.get('/admin/customer/users',{params:sParams}).success(function(data) {
                $scope.gridParam.desserts = data;
            });
        }
        //查询
        var searchDataBar = function () {
            document.querySelector('.search-sheet-custom').style.display = 'block';
            document.querySelector('.search-sheet-custom').style.top = '62px';
            document.querySelector('.search-backdrop-custom').style.top = '62px';
            document.querySelector('.search-backdrop-custom').style.display = 'block';
            var interfaceWidth = document.querySelector('.search-sheet-custom').previousElementSibling.clientWidth;
            var interfaceHeight = document.querySelector('.search-sheet-custom').previousElementSibling.clientHeight;
            setTimeout(function(){
                document.querySelector('.search-sheet-custom').style.width = interfaceWidth + 'px';
                document.querySelector('.search-backdrop-custom').style.width = interfaceWidth + 'px';
                document.querySelector('.search-backdrop-custom').style.height = interfaceHeight + 'px';
            });
            setTimeout(function(){
                document.querySelector('.search-sheet-custom').style.marginTop = '0px';
            }, 10);
            setTimeout(function(){
                document.querySelector('.btn-advanced-search').focus();
            }, 300);
        };
        $scope.customSearch = function(){
            $scope.gridParam.selected = [];
            if((angular.isUndefined($scope.name) || $scope.name === ''|| $scope.name === null) || (angular.isUndefined($scope.medicalNum) || $scope.medicalNum === ''|| $scope.medicalNum === null) || (angular.isUndefined($scope.phoneOridCard) || $scope.phoneOridCard === ''|| $scope.phoneOridCard === null)){
                $mdToast.show($mdToast.simple().textContent('姓名，病历号，手机号或身份证必填!'));
            }else{
                customSearchData();
                $scope.closeSearch();
            }
        };
        var customSearchData = function () {
            var params = {
                name: $scope.name,
                medicalNum: $scope.medicalNum,
                phoneOridCard: $scope.phoneOridCard
            };
            $http.get('/admin/customer/users',{params:params}).success(function(data) {
                $scope.gridParam.desserts = data;
            });
        };

        //重置
        $scope.customReset = function () {
            $scope.name = '';
            $scope.medicalNum = '';
            $scope.phoneOridCard = '';
        };
        //关闭搜索页面
        $scope.closeSearch =function () {
            document.querySelector('.search-sheet-custom').style.marginTop = '';
            setTimeout(function(){
                document.querySelector('.search-sheet-custom').style.display = 'none';
                document.querySelector('.search-backdrop-custom').style.display = 'none';
            }, 300);
        };

        var searchData = function (rowDatas) {
            $state.go('main.customerInfo',{'transData': rowDatas[0]});
        };
        var dbEditData = function (rowData) {
            var rowDatas = [rowData];
            searchData(rowDatas);
        };
        $scope.gridParam = {
            columns: [
                {field:'name', title: '姓名', width:'90'},
                {field:'sex', title: '性别', width:'50'},
                {field:'phone', title: '电话', width:'130'},
                {field:'idCard', title: '身份证号', width:'180'},
                {field:'medicalNum', title: '病历号', width:'130'},
                {field:'typeName', title: '类型', width:'95'},
                {field:'createTime', title: '注册时间', width:'145'},
                {field:'accountName', title: '本人姓名', width:'90'},
                {field:'accountPhone', title: '本人电话', width:'130'}
            ],
            operations: [
                {text: '查询', click: searchDataBar, iconClass: 'search_black', display: ['unSelected','singleSelected','multipleSelected']},
                {text: '查询信息', click: searchData, iconClass: 'description_black', display: ['singleSelected']}
            ],
            title: '人员详细信息',
            url: '/admin/customer/users',
            dblClick: dbEditData
        };
    };

    var mainRouter = function($stateProvider) {
        $stateProvider.state('main.customerQuery', {
            url: '^/customerQuery/customer',
            params: {'transFromInfo': null},
            templateUrl: 'modules/customerQuery/customer.html',
            controller: customerCtrl
        });
    };
    app.config(mainRouter);
})(angular.module('pea'));
