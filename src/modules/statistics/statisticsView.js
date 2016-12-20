/**
 * Created by Administrator on 2016/11/29.
 */
(function(app) {
    'use strict';

    var statisticsViewCtrl = function($scope, $http, $window) {
        $window.onresize = function(){
            $window.location.reload();
        };

        var timeDataLength;
        var timeArr = [];
        var regdata1 = [];
        var regdata2 = [];
        var reqdata1 = [];
        var reqdata2 = [];
        var opinionCountdata = [];
        var feedbackCountdata = [];
        var alipayCountdata = [];
        var alipayAmountdata = [];
        var alipayRCountdata = [];
        var alipayRAmoutdata = [];
        var wxpayCountdata = [];
        var wxpayAmountdata = [];
        var wxpayRCountdata = [];
        var wxpayRAmoutdata = [];
        var xlength = 0;
        $scope.statisticsDatas = [];

        $http.get('/msc/statistics_count',{params: {query:'{}',sort:'{statisticsDate:-1}'}}).success(function(data){
            for(var n = 0; n < data.length; n++){
                $scope.statisticsDatas.push(JSON.parse(data[n]));
            }
            timeDataLength = $scope.statisticsDatas.length;
            if($scope.statisticsDatas.length < 8){
                xlength = $scope.statisticsDatas.length;
            }else{
                xlength = 7;
            }

            //当日数据
            $scope.nowReqdata1 = $scope.statisticsDatas[timeDataLength-1].userCount;
            $scope.nowReqdata2 = $scope.statisticsDatas[timeDataLength-1].reqCount;
            $scope.nowRegdata1 = $scope.statisticsDatas[timeDataLength-1].regApptCount;
            $scope.nowRegdata2 = $scope.statisticsDatas[timeDataLength-1].regTodayCount;
            $scope.nowOpinionCountdata = $scope.statisticsDatas[timeDataLength-1].opinionCount;
            $scope.nowFeedbackCountdata = $scope.statisticsDatas[timeDataLength-1].feedbackCount;
            $scope.nowWxpayAmoutdata = $scope.statisticsDatas[timeDataLength-1].wxpayAmount;
            $scope.nowWxpayRAmoutdata = $scope.statisticsDatas[timeDataLength-1].wxpayRAmount;
            $scope.nowAlipayAmoutdata = $scope.statisticsDatas[timeDataLength-1].alipayAmount;
            $scope.nowAlipayRAmoutdata = $scope.statisticsDatas[timeDataLength-1].alipayRAmount;

            for(var i = 0; i < 7; i++) {
                timeArr.push($scope.statisticsDatas[i].statisticsDate);
                regdata1.push($scope.statisticsDatas[i].regApptCount);
                regdata2.push($scope.statisticsDatas[i].regTodayCount);
                reqdata1.push($scope.statisticsDatas[i].userCount);
                reqdata2.push($scope.statisticsDatas[i].reqCount);
                opinionCountdata.push($scope.statisticsDatas[i].opinionCount);
                feedbackCountdata.push($scope.statisticsDatas[i].feedbackCount);
                alipayCountdata.push($scope.statisticsDatas[i].alipayCount);
                alipayAmountdata.push($scope.statisticsDatas[i].alipayAmount);
                alipayRCountdata.push($scope.statisticsDatas[i].alipayRCount);
                alipayRAmoutdata.push($scope.statisticsDatas[i].alipayRAmount);
                wxpayCountdata.push($scope.statisticsDatas[i].wxpayCount);
                wxpayAmountdata.push($scope.statisticsDatas[i].wxpayAmount);
                wxpayRCountdata.push($scope.statisticsDatas[i].wxpayRCount);
                wxpayRAmoutdata.push($scope.statisticsDatas[i].wxpayRAmount);
            }
            //预约挂号数量,当日挂号数量
            $scope.reg = options('挂号数量', '预约挂号数量', '当日挂号数量', 'line', 'line', regdata1, regdata2);
            //app访客数,访问次数
            $scope.req = options('app访客数,访问次数', 'app访客数', '访问次数', 'bar', 'bar', reqdata1, reqdata2);
            //意见数量,反馈数量
            $scope.feedback = options('意见数量,反馈数量', '意见数量', '反馈数量', 'bar', 'line', opinionCountdata, feedbackCountdata);
            //支付宝平台=['支付数量','退费数量'];
            $scope.alipayc = options1('支付宝平台:支付数量,退费数量', '支付数量','退费数量', 'bar', 'bar', alipayCountdata, alipayRCountdata);
            //支付宝平台=['支付金额','退费金额'];
            $scope.alipaya = options1('支付宝平台:支付金额,退费金额', '支付金额', '退费金额', 'bar', 'bar', alipayAmountdata, alipayRAmoutdata);
            //微信平台=['支付数量','退费数量'];
            $scope.wxpayc = options1('微信平台:支付数量,退费数量', '支付数量', '退费数量', 'bar', 'bar', wxpayCountdata, wxpayRCountdata);
            //微信平台=['支付金额','退费金额'];
            $scope.wxpaya = options1('微信平台:支付金额,退费金额', '支付金额', '退费金额', 'bar', 'bar', wxpayAmountdata, wxpayRAmoutdata);
        });
        var options = function(title, dataTile1, dataTile2, chartType1, chartType2, httpData1, httpData2){
            var opt = {
                title: {
                    text: title
                },
                legend: {
                    data:[dataTile1, dataTile2]
                },
                tooltip: {
                    trigger: 'axis'
                },
                toolbox: {
                    show: true,
                    feature: {
                        dataZoom: {
                            yAxisIndex: 'none'
                        },
                        dataView: {readOnly: false,
                            optionToContent: function() {
                                var axisData = timeArr;
                                var series = [httpData1,httpData2];
                                var table = '<div style="overflow-y: scroll;height: 220px;"><table style="width:100%;text-align:center;color:#333;"><tbody><tr>' + '<td>时间</td>' + '<td>'+ dataTile1 +'</td>' + '<td>'+ dataTile2 +'</td>' + '</tr>';
                                for (var i = 0, l = axisData.length; i < l; i++) {
                                    table += '<tr>' + '<td>' + axisData[i] + '</td>' + '<td>' + series[0][i] + '</td>' + '<td>' + series[1][i] + '</td>' + '</tr>';
                                }
                                table += '</tbody></table></div>';
                                return table;
                            }
                        },
                        restore: {}
                    }
                },
                xAxis:  {
                    name: '日期',
                    type: 'category',
                    boundaryGap: true,
                    data: (function(){
                        var res = timeArr.reverse();
                        return res;
                    })()
                },
                yAxis: {
                    type: 'value',
                    axisLabel: {
                        formatter: '{value}'
                    }
                },
                series: [{
                    name: dataTile1,
                    type: chartType1,
                    data:(function (){
                        var res = httpData1.reverse();
                        return res;
                    })(),
                    markPoint: {
                        data: [
                            {type: 'max', name: '最大值'},
                            {type: 'min', name: '最小值'}
                        ]
                    },
                    markLine: {
                        data: [
                            {type: 'average', name: '平均值'}
                        ]
                    }
                },{
                    name: dataTile2,
                    type: chartType2,
                    data:(function (){
                        var res = httpData2.reverse();
                        return res;
                    })(),
                    markPoint: {
                        data: [
                            {type: 'max', name: '最大值'},
                            {type: 'min', name: '最小值'}
                        ]
                    },
                    markLine: {
                        data: [
                            {type: 'average', name: '平均值'}
                        ]
                    }
                }]
            };
            return opt;
        };
        var options1 = function(title, dataTile1, dataTile2, chartType1, chartType2, httpData1, httpData2){
            var opt = {
                title: {
                    text: title
                },
                legend: {
                    data:[dataTile1, dataTile2]
                },
                tooltip: {
                    trigger: 'axis'
                },
                toolbox: {
                    show: true,
                    feature: {
                        dataZoom: {
                            yAxisIndex: 'none'
                        },
                        dataView: {readOnly: false,
                            optionToContent: function() {
                                var axisData = timeArr;
                                var series = [httpData1,httpData2];
                                var table = '<div style="overflow-y: scroll;height: 220px;"><table style="width:100%;text-align:center;color:#333;"><tbody><tr>' + '<td>时间</td>' + '<td>'+ dataTile1 +'</td>' + '<td>'+ dataTile2 +'</td>' + '</tr>';
                                for (var i = 0, l = axisData.length; i < l; i++) {
                                    table += '<tr>' + '<td>' + axisData[i] + '</td>' + '<td>' + series[0][i] + '</td>' + '<td>' + series[1][i] + '</td>' + '</tr>';
                                }
                                table += '</tbody></table></div>';
                                return table;
                            }
                        },
                        restore: {}
                    }
                },
                color: ['#50c747', '#F3A43B'],
                xAxis:  {
                    name: '日期',
                    type: 'category',
                    boundaryGap: true,
                    data: (function(){
                        var res = timeArr.reverse();
                        return res;
                    })()
                },
                yAxis: {
                    type: 'value',
                    axisLabel: {
                        formatter: '{value}'
                    }
                },
                series: [{
                    name: dataTile1,
                    type: chartType1,
                    stack: '支付宝',
                    label: {
                        normal: {
                            show: true,
                            position: 'inside'
                        }
                    },
                    data:(function (){
                        var res = httpData1.reverse();
                        return res;
                    })()
                },{
                    name: dataTile2,
                    type: chartType2,
                    stack: '支付宝',
                    data:(function (){
                        var res = httpData2.reverse();
                        return res;
                    })()
                }]
            };
            return opt;
        };
    };

    var mainRouter = function($stateProvider) {
        $stateProvider.state('main.statisticsView', {
            url: '^/statistics/statisticsView',
            templateUrl: 'modules/statistics/statisticsView.html',
            controller: statisticsViewCtrl
        });
    };
    app.config(mainRouter);
})(angular.module('pea'));
