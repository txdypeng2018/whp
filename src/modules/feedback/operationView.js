/**
 * Created by Administrator on 2016/11/25.
 */
(function(app) {
    'use strict';

    var viewOperationCtrl = function($scope, $http, $mdDialog, $mdToast,$state,$timeout) {
        $scope.gotoList=function(){
            $state.go('main.feedbackGridOperation');
        };

        $scope.opStatus=[];
        $scope.opStatus.push({
            code: '',
            name: '全部'
        });
        $http.get('/dataBase/feedbackTypes').success(function (data) {
            for (var i = 0; i < data.length; i++) {
                $scope.opStatus.push(data[i]);
            }
        }).error(function (data) {
            $mdToast.show($mdToast.simple().textContent(data));
        });

        $scope.gotoDialog = function(userID,useroID){
            $mdDialog.show({
                controller: function($scope) {
                    //取得反馈类型
                    $http.get('/dataBase/feedbackTypes').success(function(data){
                        $scope.thefeedbackTypes=data;
                        for(var i=0;i<data.length;i++){
                            if(data[i].code==='1'){
                                $scope.selectedfeedbackType={
                                    'code': data[i].code,
                                    'name': data[i].name
                                };
                            }
                        }
                    });

                    var userdataToJson = [];
                    $http.get('/msc/service_user_opinion',{params: {query:'{ "_id":ObjectId("'+ useroID +'")}'}}).success(function(data) {
                        for(var n = 0; n < data.data.length; n++){
                            userdataToJson.push(JSON.parse(data.data[n]));
                        }
                        $scope.opinion = userdataToJson[0];

                        var historyData=[];
                        $http.get('/msc/service_user_opinion',{params: {query:'{ "userId":"'+ userID +'","opinionTime":{$lte: "'+ userdataToJson[0].opinionTime +'"}}',sort:'{opinionTime:1}'}}).success(function(data) {
                            for(var n = 0; n < data.data.length; n++){
                                historyData.push(JSON.parse(data.data[n]));
                            }
                            $scope.opinionHistory = historyData;
                        });
                    });

                    $scope.isSubmit = false;

                    $scope.cancel = function() {
                        $mdDialog.cancel();
                    };

                    $scope.submit = function() {
                        if ($scope.opinionForm.$valid && $scope.selectedfeedbackType.code==='1') {
                            $scope.isSubmit = true;
                            $scope.opinion.feedback = $scope.feedbackText;
                            $scope.opinion.status = '已反馈';
                            $scope.opinion.statusCode = '1';
                            $http.put('/service/feedbackOpinion', $scope.opinion).success(function(data) {
                                if (angular.isUndefined(data.errMsg)) {
                                    $scope.opinionHistory[$scope.opinionHistory.length-1].feedback = $scope.feedbackText;
                                    $timeout(function(){
                                        $mdDialog.hide();
                                    },2000);
                                }
                                else {
                                    $scope.isSubmit = false;
                                    $mdToast.show($mdToast.simple().textContent(data.errMsg));
                                }
                            });
                        }else if($scope.selectedfeedbackType.code ==='0'){
                            $mdToast.show($mdToast.simple().textContent('选择为未反馈，无法提交!'));
                        }else if($scope.selectedfeedbackType.code ==='2'){
                            $scope.opinion.status = '已关闭';
                            $scope.opinion.statusCode = '2';
                            $scope.isSubmit = true;
                            $http.put('/service/feedbackOpinion', $scope.opinion).success(function() {
                                $timeout(function(){
                                    $mdDialog.hide();
                                },2000);
                            });
                        }
                    };
                },
                templateUrl: 'modules/feedback/operationDialogNew.html',
                parent: angular.element(document.body),
                fullscreen: true
            });
        };

        //重置
        $scope.operationViewReset = function(){
            $scope.filterName = undefined;
            $scope.filterTel = undefined;
            $scope.filterUserOp = undefined;
            $scope.filterFeedOp = undefined;
            $scope.selectedopStatusCode = undefined;

            var dataToJson = [];
            $scope.feedBackDatasAll = [];
            $http.get('/msc/service_user_opinion',{params: {query:'{}',sort:'{opinionTime:-1}'}}).success(function(data) {
                for(var n = 0; n < data.data.length; n++){
                    dataToJson.push(JSON.parse(data.data[n]));
                }
                for (var i = 0; i < dataToJson.length; i++) {
                    $scope.feedBackDatasAll.push(dataToJson[i]);
                }
                //删除数据中，相同id的数据，保证一个id只显示在一张卡片上
                for(var m = 0; m < $scope.feedBackDatasAll.length; m++){
                    for(var j = m + 1; j < $scope.feedBackDatasAll.length; j++){
                        if($scope.feedBackDatasAll[m].userId === $scope.feedBackDatasAll[j].userId){
                            $scope.feedBackDatasAll.splice(j,1);
                            j--;
                        }
                    }
                }
                $scope.feedBackDatasAll = $scope.feedBackDatasAll.slice(0,20);
            });
        };
        //返回要过滤的条件
        var filterQuery = function () {
            var myQueryArray = [];
            if(!angular.isUndefined($scope.filterName) && $scope.filterName !== ''){
                myQueryArray.push('"userName" : /'+ $scope.filterName + '/');
            }
            if(!angular.isUndefined($scope.filterTel) && $scope.filterTel !== ''){
                myQueryArray.push('"userTel" :"'+ $scope.filterTel + '"');
            }
            if(!angular.isUndefined($scope.filterUserOp) && $scope.filterUserOp !== ''){
                myQueryArray.push('"opinion": /'+ $scope.filterUserOp + '/');
            }
            if(!angular.isUndefined($scope.filterFeedOp) && $scope.filterFeedOp !== ''){
                myQueryArray.push('"feedback": /'+ $scope.filterFeedOp + '/');
            }
            if(!angular.isUndefined($scope.selectedopStatusCode) && $scope.selectedopStatusCode !== ''){
                myQueryArray.push('"statusCode" :"'+ $scope.selectedopStatusCode + '"');
            }
            var myQuery = myQueryArray.join(',');
            return myQuery;
        };
        //查询
        $scope.operationViewSearch = function(){
            var dataToJson = [];
            $scope.feedBackDatasAll = [];
            $http.get('/msc/service_user_opinion',{params: {query:'{' + filterQuery() + '}',sort:'{opinionTime:-1}'}}).success(function(data) {
                for(var n = 0; n < data.data.length; n++){
                    dataToJson.push(JSON.parse(data.data[n]));
                }
                for (var i = 0; i < dataToJson.length; i++) {
                    $scope.feedBackDatasAll.push(dataToJson[i]);
                }
                //删除数据中，相同id的数据，保证一个id只显示在一张卡片上
                for(var m = 0; m < $scope.feedBackDatasAll.length; m++){
                    for(var j = m + 1; j < $scope.feedBackDatasAll.length; j++){
                        if($scope.feedBackDatasAll[m].userId === $scope.feedBackDatasAll[j].userId){
                            $scope.feedBackDatasAll.splice(j,1);
                            j--;
                        }
                    }
                }
                $scope.feedBackDatasAll = $scope.feedBackDatasAll.slice(0,pageSize);
            });
        };

        $scope.feedBackDatasAll = [];
        var page = 1;
        var pageSize = 20;
        var dataLength = 0;
        var dataToJson = [];
        $http.get('/msc/service_user_opinion',{params: {query:'{}',sort:'{opinionTime:-1}'}}).success(function(data) {
            for(var n = 0; n < data.data.length; n++){
                dataToJson.push(JSON.parse(data.data[n]));
            }
            for (var i = 0; i < dataToJson.length; i++) {
                $scope.feedBackDatasAll.push(dataToJson[i]);
            }
            //删除数据中，相同id的数据，保证一个id只显示在一张卡片上
            for(var m = 0; m < $scope.feedBackDatasAll.length; m++){
                for(var j = m + 1; j < $scope.feedBackDatasAll.length; j++){
                    if($scope.feedBackDatasAll[m].userId === $scope.feedBackDatasAll[j].userId){
                        $scope.feedBackDatasAll.splice(j,1);
                        j--;
                    }
                }
            }
            $scope.feedBackDatasAll = $scope.feedBackDatasAll.slice(0,page*pageSize);
        });

        $scope.showLoadMore = function(){
            $scope.loadMore = true;
        };
        $scope.text = '点击加载';
        $scope.loadMoreData = function(){
            $scope.text = '加载中，请稍后···';
            var dataToJson = [];
            $http.get('/msc/service_user_opinion',{params: {query:'{' + filterQuery() + '}',sort:'{opinionTime:-1}'}}).success(function(data) {
                for(var n = 0; n < data.data.length; n++){
                    dataToJson.push(JSON.parse(data.data[n]));
                }
                $scope.feedBackDatasAll = [];
                for (var i = 0; i < dataToJson.length; i++) {
                    $scope.feedBackDatasAll.push(dataToJson[i]);
                }
                for(var m = 0; m < $scope.feedBackDatasAll.length; m++){
                    for(var j = m + 1; j < $scope.feedBackDatasAll.length; j++){
                        if($scope.feedBackDatasAll[m].userId === $scope.feedBackDatasAll[j].userId){
                            $scope.feedBackDatasAll.splice(j,1);
                            j--;
                        }
                    }
                }
                dataLength = $scope.feedBackDatasAll.length/pageSize;
                page++;
                if(page<dataLength){
                    $scope.feedBackDatasAll = $scope.feedBackDatasAll.slice(0,page*pageSize);
                    $scope.text = '点击加载更多';
                }else{
                    $scope.text = '全部数据加载完成';
                }
            });
        };
        $scope.$on('waterfall:loadMore',function(){
            $scope.loadMoreData();
        });
    };

    var mainRouter = function($stateProvider) {
        $stateProvider.state('main.feedbackViewOperation', {
            url: '^/feedback/operationView',
            templateUrl: 'modules/feedback/operationView.html',
            controller: viewOperationCtrl
        });
    };
    app.config(mainRouter);
})(angular.module('pea'));