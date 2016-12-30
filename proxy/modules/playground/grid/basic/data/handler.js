'use strict';

var common = require('../../../../common.js');

var handler = module.exports = {};

handler.onGet = function(req, res) {
  common.jsonRes(req, res, '/all');
};

handler.onPost = function(req, res, data) {
  if (data.name === 'a') {
    res.setHeader('Content-Type', 'application/json');
    res.write(JSON.stringify({ errMsg: '姓名重复！' }));
  }
  res.statusCode = 200;
  res.end();
};

handler.onPut = function(req, res, data) {
  setTimeout(function(){
    if (data.name === 'a') {
      res.setHeader('Content-Type', 'application/json');
      res.write(JSON.stringify({ errMsg: '姓名重复！' }));
    }
    else if (data.remark === 'a') {
      res.setHeader('Content-Type', 'application/json');
      res.write(JSON.stringify({ errMsg: '备注不正确！' }));
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
