/**
 * Created by Administrator on 2016/11/29.
 */
(function(app) {
    'use strict';

    var statisticsViewCtrl = function($scope, $http, $window) {
        //计算要取得的月份
        var getMonth = function () {
            //显示在x轴上的时间
            $scope.timeForCount = [];
            //年
            $scope.timeYear = [];
            //月
            $scope.timeMonth = [];
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
                    $scope.timeForCount[i] = nowYear + '-0' + nowMonth;
                }
                else{
                    $scope.timeForCount[i] = nowYear + '-' + nowMonth;
                }
                $scope.timeYear[i] = nowYear;
                $scope.timeMonth[i] = nowMonth;
            }
            $scope.timeForCount = $scope.timeForCount.reverse();
            $scope.timeYear = $scope.timeYear.reverse();
            $scope.timeMonth = $scope.timeMonth.reverse();
        };
        //按月统计人数的数据存放到$scope.sumForCount中
        var putData = function(data){
            var dataToJson = {};
            if(!angular.isUndefined(data.data[0]) && data.data[0] !== null && data.data[0] !== ''){
                dataToJson = JSON.parse(data.data[0]);
                var str = dataToJson.CT;
                $scope.sumForCount[str.slice(0,7)] = data.count;
            }
        };
        //判断数据是否为null或isundefinded,并赋值
        var isNullorUndefinded = function (data){
            if(data === null || angular.isUndefined(data) || data === '' || isNaN(data)){
                data = 0;
            }
            return data;
        };
        //取得每月人数的http
        var httpForSumPeople = function (query) {
            $http.get('/msc/user_info',{params: {query:'{"CT":{$gte:"'+query +'-01",$lte:"'+ query +'-31"}}',limit:1}}).success(function(data){
                putData(data);
                $scope.flag++;
            });
        };
        //每3位添加逗号
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
        //添加小数点
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
        //计算数据
        var countData = function (timeArr,dataArr) {
            var newArr = [];
            for(var i =0; i < timeArr.length; i++){
                var sumForCountDataJson = {
                    time : '',
                    value : ''
                };
                sumForCountDataJson.time = timeArr[i];
                sumForCountDataJson.value = dataArr[i];
                newArr.push(sumForCountDataJson);
            }
            return newArr;
        };
        //计算数据
        var countTwoData = function (timeArr,dataArr1,dataArr2) {
            var newArr = [];
            for(var i =0; i < timeArr.length; i++){
                var sumForCountDataJson = {
                    time : '',
                    data1 : '',
                    data2 : ''
                };
                sumForCountDataJson.time = timeArr[i];
                sumForCountDataJson.data1 = dataArr1[i];
                sumForCountDataJson.data2 = dataArr2[i];
                newArr.push(sumForCountDataJson);
            }
            return newArr;
        };
        //计算数据
        var countThreeData = function (timeArr,dataArr1,dataArr2,dataArr3) {
            var newArr = [];
            for(var i =0; i < timeArr.length; i++){
                var sumForCountDataJson = {
                    time : '',
                    data1 : '',
                    data2 : '',
                    data3: ''
                };
                sumForCountDataJson.time = timeArr[i];
                sumForCountDataJson.data1 = dataArr1[i];
                sumForCountDataJson.data2 = dataArr2[i];
                sumForCountDataJson.data3 = dataArr3[i];
                newArr.push(sumForCountDataJson);
            }
            return newArr;
        };

        //取得数据
        var getData = function () {
            $scope.sumForCount = {};
            for(var x = 0; x < 7; x++){
                $scope.sumForCount[$scope.timeForCount[x]] = 0;
            }
            $http.get('/msc/user_info',{params: {query:'{}',limit:1}}).success(function(data){
                $scope.sumPeopleNum = data.count;
            });
            $scope.flag = 0;

            httpForSumPeople($scope.timeForCount[0]);
            httpForSumPeople($scope.timeForCount[1]);
            httpForSumPeople($scope.timeForCount[2]);
            httpForSumPeople($scope.timeForCount[3]);
            httpForSumPeople($scope.timeForCount[4]);
            httpForSumPeople($scope.timeForCount[5]);
            httpForSumPeople($scope.timeForCount[6]);

            $scope.$watch('flag',function(nowValue){
                if(nowValue === 7){
                    $scope.sumForCountData = [];
                    $scope.sumCount = drawSumPeopleCount($scope.timeForCount,$scope.sumForCount);
                    for(var i =0; i < $scope.timeForCount.length; i++){
                        var sumForCountDataJson = {
                            time : '',
                            value : ''
                        };
                        sumForCountDataJson.time = $scope.timeForCount[i];
                        sumForCountDataJson.value = $scope.sumForCount[$scope.timeForCount[i]];
                        $scope.sumForCountData.push(sumForCountDataJson);
                    }
                }
            });
            $scope.statisticsDatas = [];
            $http.get('/msc/statistics_count',{params: {query:'{}',sort:'{statisticsDate:-1}'}}).success(function(data){
                //取得所有数据
                var allStatisticsData = [];
                for(var n = 0; n < data.data.length; n++){
                    $scope.statisticsDatas.push(JSON.parse(data.data[n]));
                    allStatisticsData.push(JSON.parse(data.data[n]));
                }

                //当日数据
                $scope.nowReqdata1 = isNullorUndefinded($scope.statisticsDatas[0].userCount);
                $scope.nowReqdata2 = isNullorUndefinded($scope.statisticsDatas[0].reqCount);
                $scope.nowRegdata1 = isNullorUndefinded($scope.statisticsDatas[0].regApptCount);
                $scope.nowRegdata2 = isNullorUndefinded($scope.statisticsDatas[0].regTodayCount);
                $scope.nowOpinionCountdata = isNullorUndefinded($scope.statisticsDatas[0].opinionCount);
                $scope.nowFeedbackCountdata = isNullorUndefinded($scope.statisticsDatas[0].feedbackCount);
                $scope.nowWxpayAmoutdata = addComma(isNullorUndefinded($scope.statisticsDatas[0].wxpayAmount));
                $scope.nowWxpayRAmoutdata = addComma(isNullorUndefinded($scope.statisticsDatas[0].wxpayRAmount));
                $scope.nowAlipayAmoutdata = addComma(isNullorUndefinded($scope.statisticsDatas[0].alipayAmount));
                $scope.nowAlipayRAmoutdata = addComma(isNullorUndefinded($scope.statisticsDatas[0].alipayRAmount));
                $scope.nowCmbpayAmoutdata = addComma(isNullorUndefinded($scope.statisticsDatas[0].cmbAmount));
                $scope.nowCmbpayRAmoutdata = addComma(isNullorUndefinded($scope.statisticsDatas[0].cmbRAmount));

                //7天数据
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
                for(var i = 0; i < 7; i++) {
                    timeArr.push($scope.statisticsDatas[i].statisticsDate);
                    regdata1.push(isNullorUndefinded($scope.statisticsDatas[i].regApptCount));
                    regdata2.push(isNullorUndefinded($scope.statisticsDatas[i].regTodayCount));
                    reqdata1.push(isNullorUndefinded($scope.statisticsDatas[i].userCount));
                    reqdata2.push(isNullorUndefinded($scope.statisticsDatas[i].reqCount));
                    opinionCountdata.push(isNullorUndefinded($scope.statisticsDatas[i].opinionCount));
                    feedbackCountdata.push(isNullorUndefinded($scope.statisticsDatas[i].feedbackCount));
                    alipayCountdata.push(isNullorUndefinded($scope.statisticsDatas[i].alipayCount));
                    alipayAmountdata.push(addDoc(isNullorUndefinded($scope.statisticsDatas[i].alipayAmount)));
                    alipayRCountdata.push(isNullorUndefinded($scope.statisticsDatas[i].alipayRCount));
                    alipayRAmoutdata.push(addDoc(isNullorUndefinded($scope.statisticsDatas[i].alipayRAmount)));
                    wxpayCountdata.push(isNullorUndefinded($scope.statisticsDatas[i].wxpayCount));
                    wxpayAmountdata.push(addDoc(isNullorUndefinded($scope.statisticsDatas[i].wxpayAmount)));
                    wxpayRCountdata.push(isNullorUndefinded($scope.statisticsDatas[i].wxpayRCount));
                    wxpayRAmoutdata.push(addDoc(isNullorUndefinded($scope.statisticsDatas[i].wxpayRAmount)));
                    cmbCountdata.push(isNullorUndefinded($scope.statisticsDatas[i].cmbCount));
                    cmbAmountdata.push(addDoc(isNullorUndefinded($scope.statisticsDatas[i].cmbAmount)));
                    cmbRCountdata.push(isNullorUndefinded($scope.statisticsDatas[i].cmbRCount));
                    cmbRAmoutdata.push(addDoc(isNullorUndefinded($scope.statisticsDatas[i].cmbRAmount)));
                }
                var timeArrDate = [];
                for(var j = 0; j < timeArr.length; j++){
                    timeArrDate[j] = timeArr[j].substring(5);
                }
                $scope.req1day = drawOneLine(timeArrDate.reverse(),reqdata1.reverse());
                $scope.req1dayData = countData(timeArrDate,reqdata1);
                $scope.req2day = drawOneLine(timeArrDate,reqdata2.reverse());
                $scope.req2dayData = countData(timeArrDate,reqdata2);
                $scope.feedbackDay = drawTwoLine(timeArrDate,opinionCountdata.reverse(),feedbackCountdata.reverse(),'意见数量','反馈数量', 'line', 'line');
                $scope.opinionFeedbackDayData = countTwoData(timeArrDate,opinionCountdata,feedbackCountdata);
                $scope.regDay = drawTwoLineBar(timeArrDate,regdata1.reverse(),regdata2.reverse(),'预约挂号数量','当日挂号数量', 'bar', 'bar');
                $scope.regDayData = countTwoData(timeArrDate,regdata1,regdata2);
                $scope.payCountDay = drawThreeLine(timeArrDate,alipayCountdata.reverse(),wxpayCountdata.reverse(),cmbCountdata.reverse(),'支付宝支付数量','微信支付数量','一网通支付数量', 'bar', 'bar', 'bar');
                $scope.payCountDayData = countThreeData(timeArrDate,alipayCountdata,wxpayCountdata,cmbCountdata);
                $scope.payRCountDay = drawThreeLine(timeArrDate,alipayRCountdata.reverse(),wxpayRCountdata.reverse(),cmbRCountdata.reverse(),'支付宝退费数量','微信退费数量','一网通退费数量', 'bar', 'bar', 'bar');
                $scope.payRCountDayData = countThreeData(timeArrDate,alipayRCountdata,wxpayRCountdata,cmbRCountdata);
                $scope.payAmountDay = drawThreeLine(timeArrDate,alipayAmountdata.reverse(),wxpayAmountdata.reverse(),cmbAmountdata.reverse(),'支付宝支付金额','微信支付金额','一网通支付金额', 'bar', 'bar', 'bar');
                $scope.payAmountDayData = countThreeData(timeArrDate,alipayAmountdata,wxpayAmountdata,cmbAmountdata);
                $scope.payRAmountDay = drawThreeLine(timeArrDate,alipayRAmoutdata.reverse(),wxpayRAmoutdata.reverse(),cmbRAmoutdata.reverse(),'支付宝退费金额','微信退费金额','一网通退费金额', 'bar', 'bar', 'bar');
                $scope.payRAmountDayData = countThreeData(timeArrDate,alipayRAmoutdata,wxpayRAmoutdata,cmbRAmoutdata);

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
                        if (str === $scope.timeForCount[q]) {
                            allStatisticsData[p].regApptCount = isNullorUndefinded(allStatisticsData[p].regApptCount);
                            allStatisticsData[p].regTodayCount = isNullorUndefinded(allStatisticsData[p].regTodayCount);
                            allStatisticsData[p].userCount = isNullorUndefinded(allStatisticsData[p].userCount);
                            allStatisticsData[p].reqCount = isNullorUndefinded(allStatisticsData[p].reqCount);
                            allStatisticsData[p].opinionCount = isNullorUndefinded(allStatisticsData[p].opinionCount);
                            allStatisticsData[p].feedbackCount = isNullorUndefinded(allStatisticsData[p].feedbackCount);
                            allStatisticsData[p].alipayCount = isNullorUndefinded(allStatisticsData[p].alipayCount);
                            allStatisticsData[p].alipayAmount = isNullorUndefinded(allStatisticsData[p].alipayAmount);
                            allStatisticsData[p].alipayRCount = isNullorUndefinded(allStatisticsData[p].alipayRCount);
                            allStatisticsData[p].alipayRAmount = isNullorUndefinded(allStatisticsData[p].alipayRAmount);
                            allStatisticsData[p].wxpayCount = isNullorUndefinded(allStatisticsData[p].wxpayCount);
                            allStatisticsData[p].wxpayAmount = isNullorUndefinded(allStatisticsData[p].wxpayAmount);
                            allStatisticsData[p].wxpayRCount = isNullorUndefinded(allStatisticsData[p].wxpayRCount);
                            allStatisticsData[p].wxpayRAmount = isNullorUndefinded(allStatisticsData[p].wxpayRAmount);
                            allStatisticsData[p].cmbCount = isNullorUndefinded(allStatisticsData[p].cmbCount);
                            allStatisticsData[p].cmbAmount = isNullorUndefinded(allStatisticsData[p].cmbAmount);
                            allStatisticsData[p].cmbRCount = isNullorUndefinded(allStatisticsData[p].cmbRCount);
                            allStatisticsData[p].cmbRAmount = isNullorUndefinded(allStatisticsData[p].cmbRAmount);

                            regApptCountSum = regApptCountSum + parseInt(allStatisticsData[p].regApptCount);
                            regTodayCountSum = regTodayCountSum + parseInt(allStatisticsData[p].regTodayCount);
                            userCountSum = userCountSum + parseInt(allStatisticsData[p].userCount);
                            reqCountSum = reqCountSum + parseInt(allStatisticsData[p].reqCount);
                            opinionCountSum = opinionCountSum + parseInt(allStatisticsData[p].opinionCount);
                            feedbackCountSum = feedbackCountSum + parseInt(allStatisticsData[p].feedbackCount);
                            alipayCountSum = alipayCountSum + parseInt(allStatisticsData[p].alipayCount);
                            alipayAmountSum = alipayAmountSum + parseInt(allStatisticsData[p].alipayAmount)/100.0;
                            alipayRCountSum = alipayRCountSum + parseInt(allStatisticsData[p].alipayRCount);
                            alipayRAmountSum = alipayRAmountSum + parseInt(allStatisticsData[p].alipayRAmount)/100.0;
                            wxpayCountSum = wxpayCountSum + parseInt(allStatisticsData[p].wxpayCount);
                            wxpayAmountSum = wxpayAmountSum + parseInt(allStatisticsData[p].wxpayAmount)/100.0;
                            wxpayRCountSum = wxpayRCountSum + parseInt(allStatisticsData[p].wxpayRCount);
                            wxpayRAmountSum = wxpayRAmountSum + parseInt(allStatisticsData[p].wxpayRAmount)/100.0;
                            cmbCountSum = cmbCountSum + parseInt(allStatisticsData[p].cmbCount);
                            cmbAmountSum = cmbAmountSum + parseInt(allStatisticsData[p].cmbAmount)/100.0;
                            cmbRCountSum = cmbRCountSum + parseInt(allStatisticsData[p].cmbRCount);
                            cmbRAmountSum = cmbRAmountSum + parseInt(allStatisticsData[p].cmbRAmount)/100.0;
                        }
                    }
                    regApptCountArray.push(regApptCountSum);
                    regTodayCountArray.push(regTodayCountSum);
                    userCountArray.push(userCountSum);
                    reqCountArray.push(reqCountSum);
                    opinionCountArray.push(opinionCountSum);
                    feedbackCountArray.push(feedbackCountSum);
                    alipayCountArray.push(alipayCountSum);
                    alipayAmountArray.push(alipayAmountSum.toFixed(2));
                    alipayRCountArray.push(alipayRCountSum);
                    alipayRAmountArray.push(alipayRAmountSum.toFixed(2));
                    wxpayCountArray.push(wxpayCountSum);
                    wxpayAmountArray.push(wxpayAmountSum.toFixed(2));
                    wxpayRCountArray.push(wxpayRCountSum);
                    wxpayRAmountArray.push(wxpayRAmountSum.toFixed(2));
                    cmbCountArray.push(cmbCountSum);
                    cmbAmountArray.push(cmbAmountSum.toFixed(2));
                    cmbRCountArray.push(cmbRCountSum);
                    cmbRAmountArray.push(cmbRAmountSum.toFixed(2));
                }
                $scope.req1month = drawOneLine($scope.timeForCount,userCountArray);
                $scope.req1monthData = countData($scope.timeForCount,userCountArray);
                $scope.req2month = drawOneLine($scope.timeForCount,reqCountArray);
                $scope.req2monthData = countData($scope.timeForCount,reqCountArray);
                $scope.feedbackMonth = drawTwoLine($scope.timeForCount,opinionCountArray,feedbackCountArray,'意见数量','反馈数量', 'line', 'line');
                $scope.opinionFeedbackMonthData = countTwoData($scope.timeForCount,opinionCountArray,feedbackCountArray);
                $scope.regMonth = drawTwoLineBar($scope.timeForCount,regApptCountArray,regTodayCountArray,'预约挂号数量','当日挂号数量', 'bar', 'bar');
                $scope.regMonthData = countTwoData($scope.timeForCount,regApptCountArray,regTodayCountArray);
                $scope.payCountMonth = drawThreeLine($scope.timeForCount,alipayCountArray,wxpayCountArray,cmbCountArray,'支付宝支付数量','微信支付数量','一网通支付数量', 'bar', 'bar', 'bar');
                $scope.payCountMonthData = countThreeData($scope.timeForCount,alipayCountArray,wxpayCountArray,cmbCountArray);
                $scope.payRCountMonth = drawThreeLine($scope.timeForCount,alipayRCountArray,wxpayRCountArray,cmbRCountArray,'支付宝退费数量','微信退费数量','一网通退费数量', 'bar', 'bar', 'bar');
                $scope.payRCountMonthData = countThreeData($scope.timeForCount,alipayRCountArray,wxpayRCountArray,cmbRCountArray);
                $scope.payAmountMonth = drawThreeLine($scope.timeForCount,alipayAmountArray,wxpayAmountArray,cmbAmountArray,'支付宝支付金额','微信支付金额','一网通支付金额', 'bar', 'bar', 'bar');
                $scope.payAmountMonthData = countThreeData($scope.timeForCount,alipayAmountArray,wxpayAmountArray,cmbAmountArray);
                $scope.payRAmountMonth = drawThreeLine($scope.timeForCount,alipayRAmountArray,wxpayRAmountArray,cmbRAmountArray,'支付宝退费金额','微信退费金额','一网通退费金额', 'bar', 'bar', 'bar');
                $scope.payRAmountMonthData = countThreeData($scope.timeForCount,alipayRAmountArray,wxpayRAmountArray,cmbRAmountArray);
            });
        };
        var init = function () {
            //初始显示日统计的按钮样式
            $scope.dayActiveReq1 = true;
            $scope.dayActiveReq2 = true;
            $scope.dayActiveFeedback = true;
            $scope.dayActiveReg = true;
            $scope.dayActivePayCount = true;
            $scope.dayActivePayRCount = true;
            $scope.dayActivePayAmount = true;
            $scope.dayActivePayRAmount = true;
            //初始不显示月统计
            $scope.monthActiveReq1 = false;
            $scope.monthActiveReq2 = false;
            $scope.monthActiveFeedback = false;
            $scope.monthActiveReg = false;
            $scope.monthActivePayCount = false;
            $scope.monthActivePayRCount = false;
            $scope.monthActivePayAmount = false;
            $scope.monthActivePayRAmount = false;
            //初始显示图表
            $scope.sumPeopleCountFlag = true;
            $scope.visiterDataFlag = true;
            $scope.reqDataFlag = true;
            $scope.feedbackFlag = true;
            $scope.regFlag = true;
            $scope.payCountFlag = true;
            $scope.payRCountFlag = true;
            $scope.payAmountFlag = true;
            $scope.payRAmountFlag = true;
            getMonth();
            getData();
        };
        init();

        $scope.sumPeopleCountData = function () {
            if($scope.sumPeopleCountFlag === true){
                $scope.sumPeopleCountFlag = false;
            }else{
                $scope.sumPeopleCountFlag = true;
            }
        };
        $scope.visiterData = function(){
            if($scope.visiterDataFlag === true){
                $scope.visiterDataFlag = false;
            }else{
                $scope.visiterDataFlag = true;
            }
            if($scope.dayActiveReq1 === true){
                $scope.req1Data = $scope.req1dayData;
            }
            if($scope.monthActiveReq1 === true){
                $scope.req1Data = $scope.req1monthData;
            }
        };
        $scope.dayActiveReq1Clk = function () {
            $scope.dayActiveReq1 = true;
            $scope.monthActiveReq1 = false;
            $scope.req1Data = $scope.req1dayData;
        };
        $scope.monthActiveReq1Clk = function () {
            $scope.dayActiveReq1 = false;
            $scope.monthActiveReq1 = true;
            $scope.req1Data = $scope.req1monthData;
        };
        $scope.reqData = function(){
            if($scope.reqDataFlag === true){
                $scope.reqDataFlag = false;
            }else{
                $scope.reqDataFlag = true;
            }
            if($scope.dayActiveReq2 === true){
                $scope.req2Data = $scope.req2dayData;
            }
            if($scope.monthActiveReq2 === true){
                $scope.req2Data = $scope.req2monthData;
            }
        };
        $scope.dayActiveReq2Clk = function () {
            $scope.dayActiveReq2 = true;
            $scope.monthActiveReq2 = false;
            $scope.req2Data = $scope.req2dayData;
        };
        $scope.monthActiveReq2Clk = function () {
            $scope.dayActiveReq2 = false;
            $scope.monthActiveReq2 = true;
            $scope.req2Data = $scope.req2monthData;
        };
        $scope.feedbackDataClk = function(){
            if($scope.feedbackFlag === true){
                $scope.feedbackFlag = false;
            }else{
                $scope.feedbackFlag = true;
            }
            if($scope.dayActiveFeedback === true){
                $scope.feedbackData = $scope.opinionFeedbackDayData;
            }
            if($scope.monthActiveFeedback === true){
                $scope.feedbackData = $scope.opinionFeedbackMonthData;
            }
        };
        $scope.dayActiveFeedbackClk = function () {
            $scope.dayActiveFeedback = true;
            $scope.monthActiveFeedback = false;
            $scope.feedbackData = $scope.opinionFeedbackDayData;
        };
        $scope.monthActiveFeedbackClk = function () {
            $scope.dayActiveFeedback = false;
            $scope.monthActiveFeedback = true;
            $scope.feedbackData = $scope.opinionFeedbackMonthData;
        };
        $scope.regDataClk = function(){
            if($scope.regFlag === true){
                $scope.regFlag = false;
            }else{
                $scope.regFlag = true;
            }
            if($scope.dayActiveReg === true){
                $scope.regData = $scope.regDayData;
            }
            if($scope.monthActiveFeedback === true){
                $scope.regData = $scope.regMonthData;
            }
        };
        $scope.dayActiveRegClk = function () {
            $scope.dayActiveReg = true;
            $scope.monthActiveReg = false;
            $scope.regData = $scope.regDayData;
        };
        $scope.monthActiveRegClk = function () {
            $scope.dayActiveReg = false;
            $scope.monthActiveReg = true;
            $scope.regData = $scope.regMonthData;
        };
        $scope.payCountDataClk = function(){
            if($scope.payCountFlag === true){
                $scope.payCountFlag = false;
            }else{
                $scope.payCountFlag = true;
            }
            if($scope.dayActivePayCount === true){
                $scope.payCountData = $scope.payCountDayData;
            }
            if($scope.monthActivePayCount === true){
                $scope.payCountData = $scope.payCountMonthData;
            }
        };
        $scope.dayActivePayCountClk = function () {
            $scope.dayActivePayCount = true;
            $scope.monthActivePayCount = false;
            $scope.payCountData = $scope.payCountDayData;
        };
        $scope.monthActivePayCountClk = function () {
            $scope.dayActivePayCount = false;
            $scope.monthActivePayCount = true;
            $scope.payCountData = $scope.payCountMonthData;
        };
        $scope.payRCountDataClk = function(){
            if($scope.payRCountFlag === true){
                $scope.payRCountFlag = false;
            }else{
                $scope.payRCountFlag = true;
            }
            if($scope.dayActivePayRCount === true){
                $scope.payRCountData = $scope.payRCountDayData;
            }
            if($scope.monthActivePayRCount === true){
                $scope.payRCountData = $scope.payRCountMonthData;
            }
        };
        $scope.dayActivePayRCountClk = function () {
            $scope.dayActivePayRCount = true;
            $scope.monthActivePayRCount = false;
            $scope.payRCountData = $scope.payRCountDayData;
        };
        $scope.monthActivePayRCountClk = function () {
            $scope.dayActivePayRCount = false;
            $scope.monthActivePayRCount = true;
            $scope.payRCountData = $scope.payRCountMonthData;
        };
        $scope.payAmountDataClk = function(){
            if($scope.payAmountFlag === true){
                $scope.payAmountFlag = false;
            }else{
                $scope.payAmountFlag = true;
            }
            if($scope.dayActivePayAmount === true){
                $scope.payAmountData = $scope.payAmountDayData;
            }
            if($scope.monthActivePayAmount === true){
                $scope.payAmountData = $scope.payAmountMonthData;
            }
        };
        $scope.dayActivePayAmountClk = function () {
            $scope.dayActivePayAmount = true;
            $scope.monthActivePayAmount = false;
            $scope.payAmountData = $scope.payAmountDayData;
        };
        $scope.monthActivePayAmountClk = function () {
            $scope.dayActivePayAmount = false;
            $scope.monthActivePayAmount = true;
            $scope.payAmountData = $scope.payAmountMonthData;
        };
        $scope.payRAmountDataClk = function(){
            if($scope.payRAmountFlag === true){
                $scope.payRAmountFlag = false;
            }else{
                $scope.payRAmountFlag = true;
            }
            if($scope.dayRActivePayAmount === true){
                $scope.payRAmountData = $scope.payRAmountDayData;
            }
            if($scope.monthActivePayAmount === true){
                $scope.payRAmountData = $scope.payRAmountMonthData;
            }
        };
        $scope.dayActivePayRAmountClk = function () {
            $scope.dayActivePayRAmount = true;
            $scope.monthActivePayRAmount = false;
            $scope.payRAmountData = $scope.payRAmountDayData;
        };
        $scope.monthActivePayRAmountClk = function () {
            $scope.dayActivePayRAmount = false;
            $scope.monthActivePayRAmount = true;
            $scope.payRAmountData = $scope.payRAmountMonthData;
        };

        var drawSumPeopleCount = function(timeForCount,sumForCount) {
            return {
                tooltip: {
                    trigger: 'axis'
                },
                grid: {
                    top: '22%',
                    left: '3%',
                    right: '5%',
                    bottom: '6%',
                    containLabel: true
                },
                xAxis: [
                    {
                        axisLabel:{interval: 0},
                        type: 'category',
                        data: timeForCount
                    }
                ],
                yAxis: [
                    {
                        type: 'value',
                        scale: true
                    }
                ],
                series: [
                    {
                        name: '人数',
                        type: 'line',
                        areaStyle: {normal: {}},
                        data: (function () {
                            var res = [];
                            for (var i = 0; i < timeForCount.length; i++) {
                                res[i] = sumForCount[timeForCount[i]];
                            }
                            return res;
                        })(),
                        label: {
                            normal: {
                                show: true,
                                position: 'top'
                            }
                        },
                        markLine: {
                            precision: 0,
                            data: [
                                {type: 'average', name: '平均值'}
                            ]
                        }
                    }
                ]
            };
        };
        var drawOneLine = function(timeForCount,sumForCount) {
            return {
                tooltip: {
                    trigger: 'axis'
                },
                grid: {
                    top: '25%',
                    left: '3%',
                    right: '9%',
                    bottom: '8%',
                    containLabel: true
                },
                xAxis: [
                    {
                        axisLabel:{interval: 0},
                        type: 'category',
                        data: timeForCount
                    }
                ],
                yAxis: [
                    {
                        type: 'value',
                        scale: true
                    }
                ],
                series: [
                    {
                        name: '数量',
                        type: 'line',
                        data: sumForCount,
                        label: {
                            normal: {
                                show: true,
                                position: 'top'
                            }
                        },
                        markLine: {
                            precision: 0,
                            data: [
                                {type: 'average', name: '平均值'}
                            ]
                        }
                    }
                ]
            };
        };
        var drawTwoLine = function(timeForCount,data1,data2,dataTile1,dataTile2,chartType1,chartType2) {
            return {
                tooltip: {
                    trigger: 'axis'
                },
                grid: [{
                    top: '30%',
                    left: '10%',
                    right: '10%',
                    height: '25%'
                }, {
                    left: '10%',
                    right: '10%',
                    top: '55%',
                    height: '25%'
                }],
                xAxis : [
                    {
                        type : 'category',
                        boundaryGap : true,
                        axisLine: {onZero: true},
                        show: false,
                        data: timeForCount
                    },
                    {
                        gridIndex: 1,
                        type : 'category',
                        boundaryGap : true,
                        axisLine: {onZero: true},
                        data: timeForCount,
                        position: 'top'
                    }
                ],
                yAxis : [
                    {
                        name : dataTile1,
                        type : 'value'
                    },
                    {
                        gridIndex: 1,
                        name : dataTile2,
                        type : 'value',
                        inverse: true
                    }
                ],
                series : [
                    {
                        areaStyle: {normal: {}},
                        name:dataTile1,
                        type:chartType1,
                        symbolSize: 8,
                        hoverAnimation: false,
                        data: data1,
                        label: {
                            normal: {
                                show: true,
                                position: 'top'
                            }
                        }
                    },
                    {
                        areaStyle: {normal: {}},
                        name:dataTile2,
                        type:chartType2,
                        xAxisIndex: 1,
                        yAxisIndex: 1,
                        symbolSize: 8,
                        hoverAnimation: false,
                        label: {
                            normal: {
                                show: true,
                                position: 'bottom'
                            }
                        },
                        data: data2
                    }
                ]
            };
        };
        var drawTwoLineBar = function(timeForCount,data1,data2,dataTile1,dataTile2,chartType1,chartType2) {
            return {
                tooltip: {
                    trigger: 'axis'
                },
                grid: {
                    top: '35%',
                    left: '10%',
                    right: '10%'
                },
                legend: {
                    data:[dataTile1,dataTile2],
                    top:50
                },
                xAxis : [
                    {
                        type : 'category',
                        boundaryGap : true,
                        data: timeForCount
                    }
                ],
                yAxis : [
                    {
                        name : dataTile1,
                        type : 'value'
                    }
                ],
                series : [
                    {
                        name: dataTile1,
                        type: chartType1,
                        stack: 'two',
                        itemStyle: {
                            normal: {
                            },
                            emphasis: {
                                barBorderWidth: 1,
                                shadowBlur: 10,
                                shadowOffsetX: 0,
                                shadowOffsetY: 0,
                                shadowColor: 'rgba(0,0,0,0.5)'
                            }
                        },
                        data: data1
                    },
                    {
                        name: dataTile2,
                        type: chartType2,
                        stack: 'two',
                        itemStyle: {
                            normal: {
                            },
                            emphasis: {
                                barBorderWidth: 1,
                                shadowBlur: 10,
                                shadowOffsetX: 0,
                                shadowOffsetY: 0,
                                shadowColor: 'rgba(0,0,0,0.5)'
                            }
                        },
                        data: data2
                    }
                ]
            };
        };
        var drawThreeLine = function(timeForCount,data1,data2,data3,dataTile1,dataTile2,dataTile3,chartType1,chartType2,chartType3) {
            return {
                tooltip: {
                    trigger: 'axis'
                },
                legend: {
                    data:[dataTile1,dataTile2,dataTile3],
                    top:50
                },
                grid: {
                    top: '30%',
                    left: '3%',
                    right: '8%',
                    bottom: '8%',
                    containLabel: true
                },
                xAxis: [
                    {
                        axisLabel:{interval: 0},
                        type: 'category',
                        data: timeForCount
                    }
                ],
                yAxis: [
                    {
                        type: 'value'
                    }
                ],
                series: [
                    {
                        name: dataTile1,
                        type: chartType1,
                        data: data1
                    },{
                        name: dataTile2,
                        type: chartType2,
                        data: data2
                    },{
                        name: dataTile3,
                        type: chartType3,
                        data: data3
                    }
                ]
            };
        };
        $window.onresize = function(){
            $window.location.reload();
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
