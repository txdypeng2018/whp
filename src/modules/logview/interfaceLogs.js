/**
 * Created by Administrator on 2017/1/24.
 */
(function(app) {
    'use strict';

    var interfaceLogsCtrl = function($scope, $http, $mdDialog) {
        $scope.search = '';
        $scope.date = {
            start: '',
            end: ''
        };

        //日志查询
        var searchData = function() {
          $scope.gridParam.showSearchBar();
        };

        //显示详细信息
        var showData = function (rowDatas) {
            $mdDialog.show({
                controller: function($scope) {
                   $scope.CT = rowDatas[0].createTime;
                   $scope.M = rowDatas[0].methodName;
                   $scope.D = rowDatas[0].duration;
                   $scope.Q = rowDatas[0].req;
                   $scope.S = rowDatas[0].res;

                    $scope.cancel = function() {
                        $mdDialog.cancel();
                    };
                },
                templateUrl: 'modules/logview/interfaceLogsDialog.html',
                parent: angular.element(document.body),
                fullscreen: true
            });
        };
        var dbEditData = function(rowData){
            var rowDatas = [rowData];
            showData(rowDatas);
        };
        $scope.gridParam = {
            columns: [
                {field:'createTime', title: '创建时间', width: 250},
                {field:'methodName', title: '接口方法', width: 180},
                {field:'duration', title: '耗时(ms)', width: 90},
                {field:'req', title: 'request', width: 250},
                {field:'res', title: 'response', width: 250}
            ],
            operations: [
                {text: '查询', click: searchData, iconClass: 'search_black', display: ['unSelected','singleSelected','multipleSelected']},
                {text: '详细信息', click: showData, iconClass: 'description_black', display: ['singleSelected']}
            ],
            url: '/logview/wsLog',
            dblClick: dbEditData,
            isAllDataLoad: false,
            title: '接口日志',
            searchTemplate: 'modules/logview/interfaceLogsSearch.html',
            searchData: {
              search: $scope.search,
              startDate: $scope.date.start,
              endDate: $scope.date.end
            }
        };
    };

    var mainRouter = function($stateProvider) {
        $stateProvider.state('main.interfaceLogs', {
            url: '^/logview/interfaceLogs',
            templateUrl: 'modules/logview/interfaceLogs.html',
            controller: interfaceLogsCtrl
        });
    };
    app.config(mainRouter);
})(angular.module('pea'));
