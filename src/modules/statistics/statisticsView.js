/**
 * Created by Administrator on 2016/11/29.
 */
(function(app) {
    'use strict';

    var statisticsViewCtrl = function($scope, $http, $window) {
        $window.onresize = function(){
            $window.location.reload();
        };

        var comJson = {
            'userCount': '',
            'reqCount': '',
            'regApptCount': '',
            'regTodayCount': '',
            'opinionCount': '',
            'feedbackCount': '',
            'wxpayAmount': '',
            'wxpayRAmount': '',
            'wxpayCount': '',
            'wxpayRCount': '',
            'alipayCount': '',
            'alipayAmount': '',
            'alipayRCount': '',
            'alipayRAmount': '',
            'cmbCount': '',
            'cmbAmount': '',
            'cmbRCount': '',
            'cmbRAmount': ''
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
        var cmbCountdata = [];
        var cmbAmountdata = [];
        var cmbRCountdata = [];
        var cmbRAmoutdata = [];
        var xlength = 0;

        //取得要计算人数总和的日期
        var timeForCount = [];
        var timeYear = [];
        var timeMonth = [];
        for(var i = 0; i < 7; i++){
            var nowMonth = new Date().getMonth()+1;
            var nowYear = new Date().getFullYear();
            if(nowMonth - i <= 0){
                nowYear = nowYear - 1;
                nowMonth = 12 - (i - nowMonth);
            }else{
                nowMonth = nowMonth-i;
            }
            if(nowMonth < 10){
                timeForCount[i] = nowYear + '-0' + nowMonth;
            }
            else{
                timeForCount[i] = nowYear + '-' + nowMonth;
            }
            timeYear[i] = nowYear;
            timeMonth[i] = nowMonth;
        }
        $scope.sumForCount = {};
        for(var x = 0; x < 7; x++){
            $scope.sumForCount[timeForCount[x]] = 0;
        }
        $http.get('/msc/user_info',{params: {query:'{}',limit:1}}).success(function(data){
            $scope.sumPeopleNum = data.total;
        });

        $scope.flag = 0;

        $http.get('/msc/user_info',{params: {query:'{"CT":{$gte:"'+ timeForCount[0] +'-01",$lte:"'+ timeForCount[0] +'-31"}}',limit:1}}).success(function(data){
            putData(data);
            $scope.flag++;
        });
        $http.get('/msc/user_info',{params: {query:'{"CT":{$gte:"'+ timeForCount[1] +'-01",$lte:"'+ timeForCount[1] +'-31"}}',limit:1}}).success(function(data){
            putData(data);
            $scope.flag++;
        });
        $http.get('/msc/user_info',{params: {query:'{"CT":{$gte:"'+ timeForCount[2] +'-01",$lte:"'+ timeForCount[2] +'-31"}}',limit:1}}).success(function(data){
            putData(data);
            $scope.flag++;
        });
        $http.get('/msc/user_info',{params: {query:'{"CT":{$gte:"'+ timeForCount[3] +'-01",$lte:"'+ timeForCount[3] +'-31"}}',limit:1}}).success(function(data){
            putData(data);
            $scope.flag++;
        });
        $http.get('/msc/user_info',{params: {query:'{"CT":{$gte:"'+ timeForCount[4] +'-01",$lte:"'+ timeForCount[4] +'-31"}}',limit:1}}).success(function(data){
            putData(data);
            $scope.flag++;
        });
        $http.get('/msc/user_info',{params: {query:'{"CT":{$gte:"'+ timeForCount[5] +'-01",$lte:"'+ timeForCount[5] +'-31"}}',limit:1}}).success(function(data){
            putData(data);
            $scope.flag++;
        });
        $http.get('/msc/user_info',{params: {query:'{"CT":{$gte:"'+ timeForCount[6] +'-01",$lte:"'+ timeForCount[6] +'-31"}}',limit:1}}).success(function(data){
            putData(data);
            $scope.flag++;
        });

        var putData = function(data){
            var dataToJson = {};
            if(!angular.isUndefined(data.data[0])){
                dataToJson = JSON.parse(data.data[0]);
                var str = dataToJson.CT;
                $scope.sumForCount[str.slice(0,7)] = data.total;
            }
        };

        $scope.$watch('flag',function(nowValue){
            if(nowValue === 7){
                $scope.sumCount = options2(timeForCount,$scope.sumForCount);
            }
        });

        var options2 = function(timeForCount,sumForCount) {
            return {
                title: {
                    text: '每月人数统计'
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
                        dataView: {
                            readOnly: false,
                            optionToContent: function () {
                                var table = '<div style="overflow-y: scroll;height: 220px;"><table style="width:100%;text-align:center;color:#333;"><tbody><tr>' + '<td>时间</td>' + '<td>人数</td>' + '</tr>';
                                for (var i = 0; i < timeForCount.length; i++) {
                                    table += '<tr>' + '<td>' + timeForCount[i] + '</td>' + '<td>' + sumForCount[timeForCount[i]] + '</td>' + '</tr>';
                                }
                                table += '</tbody></table></div>';
                                return table;
                            }
                        },
                        restore: {}
                    }
                },
                xAxis: [
                    {
                        name: '日期',
                        type: 'category',
                        boundaryGap: true,
                        data: (function () {
                            var res = timeForCount;
                            return res.reverse();
                        })()
                    }
                ],
                yAxis: [
                    {
                        type: 'value'
                    }
                ],
                series: [
                    {
                        name: '人数',
                        type: 'line',
                        label: {
                            normal: {
                                show: true,
                                position: 'top'
                            }
                        },
                        areaStyle: {normal: {}},
                        data: (function () {
                            var res = [];
                            for (var i = 0; i < timeForCount.length; i++) {
                                res[i] = sumForCount[timeForCount[i]];
                            }
                            return res;
                        })()
                    }
                ]
            };
        };
        $scope.statisticsDatas = [];
        $http.get('/msc/statistics_count',{params: {query:'{}',sort:'{statisticsDate:-1}'}}).success(function(data){
            var allStatisticsData = [];
            for(var n = 0; n < data.data.length; n++){
                $scope.statisticsDatas.push(JSON.parse(data.data[n]));
                allStatisticsData.push(JSON.parse(data.data[n]));
            }
            timeDataLength = $scope.statisticsDatas.length;
            if($scope.statisticsDatas.length < 8){
                xlength = $scope.statisticsDatas.length;
            }else{
                xlength = 7;
            }

            for(var m = 0; m < data.data.length; m++){
                for(var x in comJson){
                    if(angular.isUndefined($scope.statisticsDatas[m][x]) || isNaN($scope.statisticsDatas[m][x])){
                        $scope.statisticsDatas[m][x] = 0;
                    }
                }
            }
            //当日数据
            $scope.nowReqdata1 = $scope.statisticsDatas[0].userCount;
            $scope.nowReqdata2 = $scope.statisticsDatas[0].reqCount;
            $scope.nowRegdata1 = $scope.statisticsDatas[0].regApptCount;
            $scope.nowRegdata2 = $scope.statisticsDatas[0].regTodayCount;
            $scope.nowOpinionCountdata = $scope.statisticsDatas[0].opinionCount;
            $scope.nowFeedbackCountdata = $scope.statisticsDatas[0].feedbackCount;

            $scope.nowWxpayAmoutdata = addComma($scope.statisticsDatas[0].wxpayAmount);
            $scope.nowWxpayRAmoutdata = addComma($scope.statisticsDatas[0].wxpayRAmount);
            $scope.nowAlipayAmoutdata = addComma($scope.statisticsDatas[0].alipayAmount);
            $scope.nowAlipayRAmoutdata = addComma($scope.statisticsDatas[0].alipayRAmount);
            $scope.nowCmbpayAmoutdata = addComma($scope.statisticsDatas[0].cmbAmount);
            $scope.nowCmbpayRAmoutdata = addComma($scope.statisticsDatas[0].cmbRAmount);

            //7天数据
            for(var i = 0; i < 7; i++) {
                timeArr.push($scope.statisticsDatas[i].statisticsDate);
                regdata1.push(transZore($scope.statisticsDatas[i].regApptCount));
                regdata2.push(transZore($scope.statisticsDatas[i].regTodayCount));
                reqdata1.push(transZore($scope.statisticsDatas[i].userCount));
                reqdata2.push(transZore($scope.statisticsDatas[i].reqCount));
                opinionCountdata.push(transZore($scope.statisticsDatas[i].opinionCount));
                feedbackCountdata.push(transZore($scope.statisticsDatas[i].feedbackCount));
                alipayCountdata.push(transZore($scope.statisticsDatas[i].alipayCount));
                alipayAmountdata.push(addDoc($scope.statisticsDatas[i].alipayAmount));
                alipayRCountdata.push(transZore($scope.statisticsDatas[i].alipayRCount));
                alipayRAmoutdata.push(addDoc($scope.statisticsDatas[i].alipayRAmount));
                wxpayCountdata.push(transZore($scope.statisticsDatas[i].wxpayCount));
                wxpayAmountdata.push(addDoc($scope.statisticsDatas[i].wxpayAmount));
                wxpayRCountdata.push(transZore($scope.statisticsDatas[i].wxpayRCount));
                wxpayRAmoutdata.push(addDoc($scope.statisticsDatas[i].wxpayRAmount));
                cmbCountdata.push(transZore($scope.statisticsDatas[i].cmbCount));
                cmbAmountdata.push(addDoc($scope.statisticsDatas[i].cmbAmount));
                cmbRCountdata.push(transZore($scope.statisticsDatas[i].cmbRCount));
                cmbRAmoutdata.push(addDoc($scope.statisticsDatas[i].cmbRAmount));
            }

            //7个月数据
            var regApptCountArray = [];
            var regTodayCountArray = [];
            var userCountArray = [];
            var reqCountArray = [];
            var opinionCountArray = [];
            var feedbackCountArray = [];
            var alipayCountArray = [];
            var alipayAmountArray = [];
            var alipayRCountArray = [];
            var alipayRAmountArray = [];
            var wxpayCountArray = [];
            var wxpayAmountArray = [];
            var wxpayRCountArray = [];
            var wxpayRAmountArray = [];
            var cmbCountArray = [];
            var cmbAmountArray = [];
            var cmbRCountArray = [];
            var cmbRAmountArray = [];
            for(var q = 0; q < 7; q++) {
                var regApptCountSum = 0;
                var regTodayCountSum = 0;
                var userCountSum = 0;
                var reqCountSum = 0;
                var opinionCountSum = 0;
                var feedbackCountSum = 0;
                var alipayCountSum = 0;
                var alipayAmountSum = 0;
                var alipayRCountSum = 0;
                var alipayRAmountSum = 0;
                var wxpayCountSum = 0;
                var wxpayAmountSum = 0;
                var wxpayRCountSum = 0;
                var wxpayRAmountSum = 0;
                var cmbCountSum = 0;
                var cmbAmountSum = 0;
                var cmbRCountSum = 0;
                var cmbRAmountSum = 0;
                for (var p = 0; p < allStatisticsData.length; p++) {
                    var str = allStatisticsData[p].statisticsDate.slice(0, 7);
                    if (str === timeForCount[q]) {
                        allStatisticsData[p].regApptCount = transZore(allStatisticsData[p].regApptCount);
                        allStatisticsData[p].regTodayCount = transZore(allStatisticsData[p].regTodayCount);
                        allStatisticsData[p].userCount = transZore(allStatisticsData[p].userCount);
                        allStatisticsData[p].reqCount = transZore(allStatisticsData[p].reqCount);
                        allStatisticsData[p].opinionCount = transZore(allStatisticsData[p].opinionCount);
                        allStatisticsData[p].feedbackCount = transZore(allStatisticsData[p].feedbackCount);
                        allStatisticsData[p].alipayCount = transZore(allStatisticsData[p].alipayCount);
                        allStatisticsData[p].alipayAmount = transZore(allStatisticsData[p].alipayAmount);
                        allStatisticsData[p].alipayRCount = transZore(allStatisticsData[p].alipayRCount);
                        allStatisticsData[p].alipayRAmount = transZore(allStatisticsData[p].alipayRAmount);
                        allStatisticsData[p].wxpayCount = transZore(allStatisticsData[p].wxpayCount);
                        allStatisticsData[p].wxpayAmount = transZore(allStatisticsData[p].wxpayAmount);
                        allStatisticsData[p].wxpayRCount = transZore(allStatisticsData[p].wxpayRCount);
                        allStatisticsData[p].wxpayRAmount = transZore(allStatisticsData[p].wxpayRAmount);
                        allStatisticsData[p].cmbCount = transZore(allStatisticsData[p].cmbCount);
                        allStatisticsData[p].cmbAmount = transZore(allStatisticsData[p].cmbAmount);
                        allStatisticsData[p].cmbRCount = transZore(allStatisticsData[p].cmbRCount);
                        allStatisticsData[p].cmbRAmount = transZore(allStatisticsData[p].cmbRAmount);

                        regApptCountSum = regApptCountSum + parseInt(allStatisticsData[p].regApptCount);
                        regTodayCountSum = regTodayCountSum + parseInt(allStatisticsData[p].regTodayCount);
                        userCountSum = userCountSum + parseInt(allStatisticsData[p].userCount);
                        reqCountSum = reqCountSum + parseInt(allStatisticsData[p].reqCount);
                        opinionCountSum = opinionCountSum + parseInt(allStatisticsData[p].opinionCount);
                        feedbackCountSum = feedbackCountSum + parseInt(allStatisticsData[p].feedbackCount);
                        alipayCountSum = alipayCountSum + parseInt(allStatisticsData[p].alipayCount);
                        alipayAmountSum = alipayAmountSum + (parseInt(allStatisticsData[p].alipayAmount)/100.0).toFixed(2);
                        alipayRCountSum = alipayRCountSum + parseInt(allStatisticsData[p].alipayRCount);
                        alipayRAmountSum = alipayRAmountSum + (parseInt(allStatisticsData[p].alipayRAmount)/100.0).toFixed(2);
                        wxpayCountSum = wxpayCountSum + parseInt(allStatisticsData[p].wxpayCount);
                        wxpayAmountSum = wxpayAmountSum + (parseInt(allStatisticsData[p].wxpayAmount)/100.0).toFixed(2);
                        wxpayRCountSum = wxpayRCountSum + parseInt(allStatisticsData[p].wxpayRCount);
                        wxpayRAmountSum = wxpayRAmountSum + (parseInt(allStatisticsData[p].wxpayRAmount)/100.0).toFixed(2);
                        cmbCountSum = cmbCountSum + parseInt(allStatisticsData[p].cmbCount);
                        cmbAmountSum = cmbAmountSum + (parseInt(allStatisticsData[p].cmbAmount)/100.0).toFixed(2);
                        cmbRCountSum = cmbRCountSum + parseInt(allStatisticsData[p].cmbRCount);
                        cmbRAmountSum = cmbRAmountSum + (parseInt(allStatisticsData[p].cmbRAmount)/100.0).toFixed(2);
                    }
                }
                regApptCountArray.push(regApptCountSum);
                regTodayCountArray.push(regTodayCountSum);
                userCountArray.push(userCountSum);
                reqCountArray.push(reqCountSum);
                opinionCountArray.push(opinionCountSum);
                feedbackCountArray.push(feedbackCountSum);
                alipayCountArray.push(alipayCountSum);
                alipayAmountArray.push(alipayAmountSum);
                alipayRCountArray.push(alipayRCountSum);
                alipayRAmountArray.push(alipayRAmountSum);
                wxpayCountArray.push(wxpayCountSum);
                wxpayAmountArray.push(wxpayAmountSum);
                wxpayRCountArray.push(wxpayRCountSum);
                wxpayRAmountArray.push(wxpayRAmountSum);
                cmbCountArray.push(cmbCountSum);
                cmbAmountArray.push(cmbAmountSum);
                cmbRCountArray.push(cmbRCountSum);
                cmbRAmountArray.push(cmbRAmountSum);
            }

            //预约挂号数量,当日挂号数量
            $scope.reg = optionsNew('挂号数量', '预约挂号数量', '当日挂号数量', 'line', 'line', timeArr, regdata1, regdata2, regApptCountArray, regTodayCountArray, timeForCount);
            //app访客数,访问次数
            $scope.req = optionsNew('app访客数,访问次数', 'app访客数', '访问次数', 'bar', 'bar', timeArr, reqdata1, reqdata2, userCountArray, reqCountArray, timeForCount);
            //意见数量,反馈数量
            $scope.feedback = optionsNew('意见数量,反馈数量', '意见数量', '反馈数量', 'bar', 'line', timeArr, opinionCountdata, feedbackCountdata, opinionCountArray, feedbackCountArray, timeForCount);
            //支付宝平台=['支付数量','退费数量'];
            $scope.alipayc = optionsNew1('支付宝平台:支付数量,退费数量', '支付数量','退费数量', 'bar', 'bar', timeArr, alipayCountdata, alipayRCountdata, alipayCountArray, alipayRCountArray, timeForCount);
            //支付宝平台=['支付金额','退费金额'];
            $scope.alipaya = optionsNew1('支付宝平台:支付金额,退费金额', '支付金额', '退费金额', 'bar', 'bar', timeArr, alipayAmountdata, alipayRAmoutdata, alipayAmountArray, alipayRAmountArray, timeForCount);
            //微信平台=['支付数量','退费数量'];
            $scope.wxpayc = optionsNew1('微信平台:支付数量,退费数量', '支付数量', '退费数量', 'bar', 'bar', timeArr, wxpayCountdata, wxpayRCountdata, wxpayCountArray, wxpayRCountArray, timeForCount);
            //微信平台=['支付金额','退费金额'];
            $scope.wxpaya = optionsNew1('微信平台:支付金额,退费金额', '支付金额', '退费金额', 'bar', 'bar', timeArr, wxpayAmountdata, wxpayRAmoutdata, wxpayAmountArray, wxpayRAmountArray, timeForCount);
            //一网通平台=['支付数量','退费数量'];
            $scope.cmbpayc = optionsNew1('一网通平台:支付数量,退费数量', '支付数量', '退费数量', 'bar', 'bar', timeArr, cmbCountdata, cmbRCountdata, cmbCountArray, cmbRCountArray, timeForCount);
            //一网通平台=['支付金额','退费金额'];
            $scope.cmbpaya = optionsNew1('一网通平台:支付金额,退费金额', '支付金额', '退费金额', 'bar', 'bar', timeArr, cmbAmountdata, cmbRAmoutdata, cmbAmountArray, cmbRAmountArray, timeForCount);
        });

        var optionsNew = function(title, dataTile1, dataTile2, chartType1, chartType2, timeArr, regdata1, regdata2, regApptCountArray, regTodayCountArray, timeForCount){
            var timeArrNew = [];
            var httpData1 = [];
            var httpData2 = [];
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
                        myToolMonth: {
                            title: '按月统计',
                            icon: 'path://M20 80 L30,30 L60,30 L60,80 L60,45 L35,45 L60,45 L60,60 L30 60',
                            onclick: function (){
                                for(var i = 0; i< timeForCount.length; i++){
                                    timeArrNew[i] = timeForCount[i];
                                    httpData1[i] = regApptCountArray[timeForCount.length - 1 - i];
                                    httpData2[i] = regTodayCountArray[timeForCount.length - 1 - i];
                                }
                            }
                        },
                        myToolDay: {
                            title: '按日统计',
                            icon: 'path://M30,30 L60,30 L60,70 L30,70 L30,30 L30,50 L60,50 ',
                            onclick: function (){
                                for(var i = 0; i< timeArr.length; i++){
                                    timeArrNew[i] = timeArr[timeArr.length - 1 - i];
                                    httpData1[i] = regdata1[timeArr.length - 1 - i];
                                    httpData2[i] = regdata2[timeArr.length - 1 - i];
                                }
                            }
                        },
                        dataZoom: {
                            yAxisIndex: 'none'
                        },
                        dataView: {readOnly: false,
                            optionToContent: function() {
                                var axisData = timeArrNew;
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
                        for(var i = 0; i< timeArr.length; i++){
                            timeArrNew[i] = timeArr[i];
                        }
                        var res = timeArrNew.reverse();
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
                        for(var i = 0; i< timeArr.length; i++){
                            httpData1[i] = regdata1[i];
                        }
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
                        for(var i = 0; i< timeArr.length; i++){
                            httpData2[i] = regdata2[i];
                        }
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
        var optionsNew1 = function(title, dataTile1, dataTile2, chartType1, chartType2, timeArr, regdata1, regdata2, regApptCountArray, regTodayCountArray, timeForCount){
            var timeArrNew = [];
            var httpData1 = [];
            var httpData2 = [];
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
                        myToolMonth: {
                            title: '按月统计',
                            icon: 'path://M20 80 L30,30 L60,30 L60,80 L60,45 L35,45 L60,45 L60,60 L30 60',
                            onclick: function (){
                                for(var i = 0; i< timeForCount.length; i++){
                                    timeArrNew[i] = timeForCount[i];
                                    httpData1[i] = regApptCountArray[timeForCount.length - 1 - i];
                                    httpData2[i] = regTodayCountArray[timeForCount.length - 1 - i];
                                }
                            }
                        },
                        myToolDay: {
                            title: '按日统计',
                            icon: 'path://M30,30 L60,30 L60,70 L30,70 L30,30 L30,50 L60,50 ',
                            onclick: function (){
                                for(var i = 0; i< timeArr.length; i++){
                                    timeArrNew[i] = timeArr[timeArr.length - 1 - i];
                                    httpData1[i] = regdata1[timeArr.length - 1 - i];
                                    httpData2[i] = regdata2[timeArr.length - 1 - i];
                                }
                            }
                        },
                        dataZoom: {
                            yAxisIndex: 'none'
                        },
                        dataView: {readOnly: false,
                            optionToContent: function() {
                                var axisData = timeArrNew;
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
                        for(var i = 0; i< timeArr.length; i++){
                            timeArrNew[i] = timeArr[i];
                        }
                        var res = timeArrNew.reverse();
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
                        for(var i = 0; i< timeArr.length; i++){
                            httpData1[i] = regdata1[i];
                        }
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
                        for(var i = 0; i< timeArr.length; i++){
                            httpData2[i] = regdata2[i];
                        }
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
        var addDoc = function(str){
            var strIntDoc = (str/100).toString();
            var strInt = strIntDoc.split('.')[0];
            var strDoc = strIntDoc.split('.')[1];
            if(angular.isUndefined(strDoc)){
                strDoc = new Array(strDoc,'00').join('');
            }else if(strDoc.length === 1) {
                strDoc = new Array(strDoc, '0').join('');
            }
            return new Array(strInt,strDoc).join('.');
        };
        var transZore = function(str){
            if(str === null || angular.isUndefined(str) || str === ''){
                str = '0';
            }
            return str;
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
