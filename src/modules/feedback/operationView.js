/**
 * Created by Administrator on 2016/11/25.
 */
(function(app) {
    'use strict';

    var viewOperationCtrl = function($scope, $http, $mdDialog, $mdToast, $state, $timeout, $window, $rootScope) {
        $rootScope.refreshFlag = 0;

        $scope.$watch('refreshFlag',function (newValue) {
            if(newValue === 1){
                $scope.gotoRefresh();
                $rootScope.refreshFlag = 0;
            }
        });

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
                controller: function($scope, $rootScope) {
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
                    $scope.closeDialog = function(){
                        $scope.opinion.status = '已关闭';
                        $scope.opinion.statusCode = '2';
                        for(var j = 0; j < $rootScope.feedBackDatasAll.length; j++){
                            if($rootScope.feedBackDatasAll[j]._id.$oid === useroID){
                                $rootScope.feedBackDatasAll[j].statusCode = '2';
                            }
                        }
                        $scope.opinion.id = useroID;
                        $scope.isSubmit = true;
                        $http.put('/service/feedbackOpinion', $scope.opinion).success(function() {
                            $timeout(function(){
                                $mdDialog.hide();
                                $rootScope.refreshFlag = 1;
                            },1000);
                        });
                    };

                    $scope.submit = function() {
                        if ($scope.opinionForm.$valid && $scope.selectedfeedbackType.code==='1') {
                            $scope.isSubmit = true;
                            $scope.opinion.feedback = $scope.feedbackText;
                            $scope.opinion.status = '已反馈';
                            $scope.opinion.statusCode = '1';
                            for(var i = 0; i < $rootScope.feedBackDatasAll.length; i++){
                                if($rootScope.feedBackDatasAll[i]._id.$oid === useroID){
                                    $rootScope.feedBackDatasAll[i].statusCode = '1';
                                }
                            }
                            $scope.opinion.id = useroID;
                            $http.put('/service/feedbackOpinion', $scope.opinion).success(function(data) {
                                if (angular.isUndefined(data.errMsg)) {
                                    $scope.opinionHistory[$scope.opinionHistory.length-1].feedback = $scope.feedbackText;
                                    $mdDialog.hide();
                                    $rootScope.refreshFlag = 1;
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
                            for(var j = 0; j < $rootScope.feedBackDatasAll.length; j++){
                                if($rootScope.feedBackDatasAll[j]._id.$oid === useroID){
                                    $rootScope.feedBackDatasAll[j].statusCode = '2';
                                }
                            }
                            $scope.opinion.id = useroID;
                            $scope.isSubmit = true;
                            $http.put('/service/feedbackOpinion', $scope.opinion).success(function() {
                                $timeout(function(){
                                    $mdDialog.hide();
                                    $rootScope.refreshFlag = 1;
                                },1000);
                            });
                        }
                    };
                    $scope.scrollToEnd = function(){
                        var div = document.getElementById('newOpDialog');
                        div.parentNode.scrollTop = div.parentNode.scrollHeight;
                    };
                },
                templateUrl: 'modules/feedback/operationDialogNew.html',
                parent: angular.element(document.body),
                fullscreen: true
            });
        };

        //刷新
        $scope.gotoRefresh = function(){
            $scope.operationViewSearch();
        };

        //重置
        $scope.operationViewReset = function(){
            $scope.filterName = undefined;
            $scope.filterTel = undefined;
            $scope.filterUserOp = undefined;
            $scope.filterFeedOp = undefined;
            $scope.selectedopStatusCode = undefined;

            var dataToJson = [];
            $rootScope.feedBackDatasAll = [];
            $http.get('/msc/service_user_opinion',{params: {query:'{}',sort:'{opinionTime:-1}'}}).success(function(data) {
                for(var n = 0; n < data.data.length; n++){
                    dataToJson.push(JSON.parse(data.data[n]));
                }
                for (var i = 0; i < dataToJson.length; i++) {
                    $rootScope.feedBackDatasAll.push(dataToJson[i]);
                }
                //删除数据中，相同id的数据，保证一个id只显示在一张卡片上
                for(var m = 0; m < $rootScope.feedBackDatasAll.length; m++){
                    for(var j = m + 1; j < $rootScope.feedBackDatasAll.length; j++){
                        if($rootScope.feedBackDatasAll[m].userId === $rootScope.feedBackDatasAll[j].userId){
                            $rootScope.feedBackDatasAll.splice(j,1);
                            j--;
                        }
                    }
                }
                $rootScope.feedBackDatasAll = $rootScope.feedBackDatasAll.slice(0,20);
            });
        };
        //返回要过滤的条件
        var filterQuery = function () {
            var myQuery = '';
            if(!angular.isUndefined($scope.search) && $scope.search !== ''){
                if(!angular.isUndefined($scope.selectedopStatusCode) && $scope.selectedopStatusCode !== '') {
                    myQuery = '$or: [{"userName": /' + $scope.search + '/}, {"userTel": /' + $scope.search + '/}, {"opinion": /' + $scope.search + '/}, {"feedback": /' + $scope.search + '/}], "statusCode": "' + $scope.selectedopStatusCode + '"';
                }else{
                    myQuery = '$or: [{"userName": /' + $scope.search + '/}, {"userTel": /' + $scope.search + '/}, {"opinion": /' + $scope.search + '/}, {"feedback": /' + $scope.search + '/}]';
                }
            }else{
                if(!angular.isUndefined($scope.selectedopStatusCode) && $scope.selectedopStatusCode !== ''){
                    myQuery = '"statusCode":"'+$scope.selectedopStatusCode+'"';
                }else{
                    myQuery = '';
                }
            }
            return myQuery;
        };
        //查询
        $scope.operationViewSearch = function(){
            var dataToJson = [];
            var pageS = 1;
            var dataLengthS = 0;
            $rootScope.feedBackDatasAll = [];
            $http.get('/msc/service_user_opinion',{params: {query:'{' + filterQuery() + '}',sort:'{opinionTime:-1}'}}).success(function(data) {
                for(var n = 0; n < data.data.length; n++){
                    dataToJson.push(JSON.parse(data.data[n]));
                }
                for (var i = 0; i < dataToJson.length; i++) {
                    $rootScope.feedBackDatasAll.push(dataToJson[i]);
                }
                //删除数据中，相同id的数据，保证一个id只显示在一张卡片上
                for(var m = 0; m < $rootScope.feedBackDatasAll.length; m++){
                    for(var j = m + 1; j < $rootScope.feedBackDatasAll.length; j++){
                        if($rootScope.feedBackDatasAll[m].userId === $rootScope.feedBackDatasAll[j].userId){
                            $rootScope.feedBackDatasAll.splice(j,1);
                            j--;
                        }
                    }
                }
                dataLengthS = $rootScope.feedBackDatasAll.length/pageSize;
                $rootScope.feedBackDatasAll = $rootScope.feedBackDatasAll.slice(0,pageS*pageSize);
                pageS++;
                if(pageS<dataLengthS){
                    $rootScope.feedBackDatasAll = $rootScope.feedBackDatasAll.slice(0,pageS*pageSize);
                    $scope.waterfall.hasMore = true;
                }else{
                    $scope.waterfall.hasMore = false;
                }
            }).finally(function() {
                $scope.$broadcast('peaWaterfall.loadFinished');
            });
        };
        //查询事件
        $scope.$watch('selectedopStatusCode', function(newValue, oldValue) {
            if(newValue !== oldValue){
                $scope.operationViewSearch();
            }
        });
        $scope.searchKeyup = function(e) {
            var keyCode = window.event?e.keyCode:e.which;
            if(keyCode === 13){
                $scope.operationViewSearch();
            }
        };

        $rootScope.feedBackDatasAll = [];
        var page = 1;
        var pageSize = 20;
        var dataLength = 0;
        var getDate = function(param) {
            var dataToJson = [];
            $http.get('/msc/service_user_opinion',{params: {query:'{' + param + '}',sort:'{opinionTime:-1}'}}).success(function(data) {
                for(var n = 0; n < data.data.length; n++){
                    dataToJson.push(JSON.parse(data.data[n]));
                }
                for (var i = 0; i < dataToJson.length; i++) {
                    $rootScope.feedBackDatasAll.push(dataToJson[i]);
                }
                //删除数据中，相同id的数据，保证一个id只显示在一张卡片上
                for(var m = 0; m < $rootScope.feedBackDatasAll.length; m++){
                    for(var j = m + 1; j < $rootScope.feedBackDatasAll.length; j++){
                        if($rootScope.feedBackDatasAll[m].userId === $rootScope.feedBackDatasAll[j].userId){
                            $rootScope.feedBackDatasAll.splice(j,1);
                            j--;
                        }
                    }
                }
                dataLength = $rootScope.feedBackDatasAll.length/pageSize;
                $rootScope.feedBackDatasAll = $rootScope.feedBackDatasAll.slice(0,page*pageSize);
                page++;
                if(page<dataLength){
                    $rootScope.feedBackDatasAll = $rootScope.feedBackDatasAll.slice(0,page*pageSize);
                    $scope.waterfall.hasMore = true;
                }else{
                    $scope.waterfall.hasMore = false;
                }
            }).finally(function() {
                $scope.$broadcast('peaWaterfall.loadFinished');
            });
        };

        $scope.waterfall = {
            pageNo: 1,
            hasMore: true,
            init: function () {
                var queryStr = '"statusCode":"0"' ;
                $scope.selectedopStatusCode = '0';
                $scope.waterfall.hasMore = true;
                getDate(queryStr);
            },
            loadMore: function () {
                var queryStr = '';
                if(!angular.isUndefined(filterQuery()) && filterQuery() !== ''){
                    queryStr = filterQuery();
                }else{
                    if(angular.isUndefined($scope.selectedopStatusCode) || $scope.selectedopStatusCode === ''){
                        queryStr = '';
                    }else{
                        queryStr = '"statusCode":"0"' ;
                    }
                }
                getDate(queryStr);
            }
        };

        $scope.waterfall.init();
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
