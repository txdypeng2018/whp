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

        $scope.gotoDialog=function(userID){
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
                    $http.get('/msc/service_user_opinion',{params: {query:'{userId:'+ userID +'}'}}).success(function(data) {
                        for(var n = 0; n < data.length; n++){
                            userdataToJson.push(JSON.parse(data[n]));
                        }
                        $scope.opinion = userdataToJson[0];
                    });

                    $scope.isSubmit = false;

                    $scope.cancel = function() {
                        $mdDialog.cancel();
                    };

                    $scope.submit = function() {
                        $scope.opinion.statusCode=$scope.selectedfeedbackType.code;
                        if ($scope.opinionForm.$valid && $scope.selectedfeedbackType.code==='1') {
                            $scope.isSubmit = true;
                            $http.put('/service/feedbackOpinion', $scope.opinion).success(function(data) {
                                if (angular.isUndefined(data.errMsg)) {
                                    $mdDialog.hide();
                                }
                                else {
                                    $scope.isSubmit = false;
                                    $mdToast.show($mdToast.simple().textContent(data.errMsg));
                                }
                            });
                        }else if($scope.selectedfeedbackType.code ==='0'){
                            $mdToast.show($mdToast.simple().textContent('选择为未反馈，无法提交!'));
                        }else if($scope.selectedfeedbackType.code ==='2'){
                            $scope.isSubmit = true;
                            $http.put('/service/feedbackOpinion', $scope.opinion).success(function() {
                                $mdDialog.hide();
                            });
                        }
                    };
                },
                templateUrl: 'modules/feedback/operationDialog.html',
                parent: angular.element(document.body),
                fullscreen: true
            });
        };

        $scope.feedBackDatasAll=[];
        var page = 1;
        var pageSize = 20;
        var dataLength = 0;
        var dataToJson = [];
        $http.get('/msc/service_user_opinion',{params: {query:'{}'}}).success(function(data) {
            for(var n = 0; n < data.length; n++){
                dataToJson.push(JSON.parse(data[n]));
            }
            dataLength = dataToJson.length/pageSize;
            $scope.results = dataToJson.slice(0,page*pageSize);
            for (var i = 0; i < $scope.results.length; i++) {
                $scope.feedBackDatasAll.push($scope.results[i]);
            }
        });

        $scope.text = '点击加载';
        $scope.loadMore = true;
        $scope.loadMoreData = function(){
            $scope.text = '加载中，请稍后···';
            $timeout(function(){
                if(page<dataLength){
                    page++;
                    $http.get('/msc/service_user_opinion',{params: {query:'{}'}}).success(function(data) {
                        var results = data.slice((page-1)*pageSize,page*pageSize);
                        var resultsToJson = [];
                        for(var n = 0; n < results.length; n++){
                            resultsToJson.push(JSON.parse(results[n]));
                        }
                        for (var i = 0; i < resultsToJson.length; i++) {
                            $scope.feedBackDatasAll.push(resultsToJson[i]);
                        }
                    });
                    $scope.text = '点击加载更多';
                }else{
                    $scope.text = '全部数据加载完成';
                }
            },1500);

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
