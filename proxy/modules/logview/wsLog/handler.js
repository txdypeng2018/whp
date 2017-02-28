/**
 * Created by Administrator on 2017/2/28.
 */
'use strict';

var common = require('../../common.js');

var handler = module.exports = {};

var addDatas = [];

handler.onGet = function(req, res) {
    var query = common.parseUrl(req).query;
    if(query.search === undefined && query.startDate === undefined && query.endDate === undefined){
        if (query.pageNo === '1' && query.pageSize === '20' && query.M === 'SMS') {
            common.jsonRes(req, res, '/page_3');
        }
        if (query.pageNo === '2' && query.pageSize === '20' && query.M === 'SMS') {
            common.jsonRes(req, res, '/page_4');
        }
        if (query.pageNo === '1' && query.pageSize === '20' && query.M === undefined) {
            common.jsonRes(req, res, '/page_1');
        }
        if (query.pageNo === '2' && query.pageSize === '20' && query.M === undefined) {
            common.jsonRes(req, res, '/page_2');
        }
    }else{
        var prefix = '/all';
        if(query.M === 'SMS'){
            prefix = '/all_1';
        }else{
            prefix = '/all';
        }
        var path = common.url2path(req, 'proxy') + prefix + '.json';
        var fs = require('fs');
        fs.access(path, function(err) {
            if (!err) {
                var resBody = fs.readFileSync(path);
                res.statusCode = 200;
                res.setHeader('Content-Type', 'application/json');
                var resBodyJson = JSON.parse(resBody);
                if(addDatas.length > 0){
                    for (var i = 0 ; i < addDatas.length ; i) {
                        resBodyJson.data.unshift(addDatas[i]);
                    }
                }
                var search = common.parseUrl(req).query.search;
                var startDate = common.parseUrl(req).query.startDate;
                var endDate = common.parseUrl(req).query.endDate;
                if(search !== undefined && (startDate === undefined && endDate === undefined || startDate === '' && endDate === '')){
                    var newBodyJson1 = {'data':[]};
                    for (var a = 0 ; a < resBodyJson.count ; a++) {
                        if(resBodyJson.data[a].Q.indexOf(search) >= 0 || resBodyJson.data[a].S.indexOf(search) >= 0 || resBodyJson.data[a].M.indexOf(search) >= 0){
                            newBodyJson1.data.push(resBodyJson.data[a]);
                        }
                    }
                    resBodyJson = newBodyJson1;
                }else if(startDate !== undefined && endDate !== undefined && search === undefined){
                    var newBodyJson2 = {'data':[]};
                    startDate = startDate.replace(/-/g,'/');
                    var startDate1 = new Date(startDate).getTime();
                    endDate = endDate.replace(/-/g,'/');
                    var endDate1 = new Date(endDate).getTime();
                    for (var b = 0 ; b < resBodyJson.count ; b++) {
                        var dateTime = resBodyJson.data[b].CT;
                        dateTime = new Date(dateTime.replace(/-/g,'/')).getTime();
                        if(startDate1 < dateTime && dateTime < endDate1){
                            newBodyJson2.data.push(resBodyJson.data[b]);
                        }
                    }
                    resBodyJson = newBodyJson2;
                }else if(search !== undefined && startDate !== undefined && endDate !== undefined){
                    var newBodyJson3 = {'data':[]};
                    startDate = startDate.replace(/-/g,'/');
                    var startDate2 = new Date(startDate).getTime();
                    endDate = endDate.replace(/-/g,'/');
                    var endDate2 = new Date(endDate).getTime();
                    for (var j = 0 ; j < resBodyJson.count ; j++) {
                        var dateTime1 = resBodyJson.data[j].CT;
                        dateTime1 = new Date(dateTime1.replace(/-/g,'/')).getTime();
                        if((startDate2 < dateTime1) && (dateTime1 < endDate2) && (resBodyJson.data[j].Q.indexOf(search) >= 0 || resBodyJson.data[j].S.indexOf(search) >= 0 || resBodyJson.data[j].M.indexOf(search) >= 0)){
                            newBodyJson3.data.push(resBodyJson.data[j]);
                        }
                    }
                    resBodyJson = newBodyJson3;
                }

                var resBodyStr = JSON.stringify(resBodyJson);
                res.write(resBodyStr);
                res.end();
            }else {
                res.statusCode = 200;
                res.setHeader('Content-Type', 'application/json');
                res.end();
            }
        });
    }

};