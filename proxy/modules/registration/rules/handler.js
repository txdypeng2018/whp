'use strict';

var common = require('../../common.js');

var handler = module.exports = {};

var addDatas = [];

handler.onGet = function(req, res) {
    var prefix = '/all';
    var path = common.url2path(req, 'proxy') + prefix + '.json';
    var fs = require('fs');
    fs.access(path, function(err) {
        if (!err) {
            var resBody = fs.readFileSync(path);
            res.statusCode = 200;
            res.setHeader('Content-Type', 'application/json');
            var resBodyJson = JSON.parse(resBody);
            if(addDatas.length > 0){
                for (var i = 0 ; i < addDatas.length ; i++) {
                    resBodyJson.data.unshift(addDatas[i]);
                }
            }
            var queryName = common.parseUrl(req).query.name;
            var queryDescription = common.parseUrl(req).query.description;
            if(queryName !== undefined && queryDescription === undefined){
              var newBodyJson1 = {'data':[]};
              for (var a = 0 ; a < resBodyJson.count ; a++) {
                  if(resBodyJson.data[a].name.indexOf(queryName) >= 0){
                      newBodyJson1.data.push(resBodyJson.data[a]);
                  }
              }
              resBodyJson = newBodyJson1;
            }else if(queryDescription !== undefined && queryName === undefined){
                var newBodyJson2 = {'data':[]};
                for (var b = 0 ; b < resBodyJson.count ; b++) {
                    if(resBodyJson.data[b].description !== null && resBodyJson.data[b].description.indexOf(queryDescription) >= 0){
                        newBodyJson2.data.push(resBodyJson.data[b]);
                    }
                }
                resBodyJson = newBodyJson2;
            }else if(queryName !== undefined && queryDescription !== undefined && queryName !== '' && queryDescription !== ''){
                var newBodyJson = {'data':[]};
                for (var j = 0 ; j < resBodyJson.count ; j++) {
                    if((resBodyJson.data[j].description !== null) && (resBodyJson.data[j].name.indexOf(queryName) >= 0) && (resBodyJson.data[j].description.indexOf(queryDescription) >= 0)){
                        newBodyJson.data.push(resBodyJson.data[j]);
                    }
                }
                resBodyJson = newBodyJson;
            }

            var resBodyStr = JSON.stringify(resBodyJson);
            res.write(resBodyStr);
            res.end();
        } else {
            res.statusCode = 200;
            res.setHeader('Content-Type', 'application/json');
            res.end();
        }
    });
};

handler.onPost = function(req, res, data) {
  setTimeout(function(){
    if (data.ruleName === '儿童挂号约束') {
      res.setHeader('Content-Type', 'application/json');
      res.write(JSON.stringify({ errMsg: '挂号规则名称重复！' }));
    }
    addDatas.push(data);
    res.statusCode = 200;
    res.end();
  }, 100);
};

handler.onPut = function(req, res, data) {
  setTimeout(function(){
    if (data.ruleName === '儿童挂号约束') {
      res.setHeader('Content-Type', 'application/json');
      res.write(JSON.stringify({ errMsg: '挂号规则名称重复！' }));
    }
    res.statusCode = 200;
    res.end();
  }, 100);
};

handler.onDelete = function(req, res) {
  var query = common.parseUrl(req).query;
  if (query.ids.split(',').indexOf('2') >= 0) {
    res.setHeader('Content-Type', 'application/json');
    res.write(JSON.stringify({ errMsg: '选中的数据不能删除！' }));
  }
  res.statusCode = 200;
  res.end();
};
