'use strict';

var common = require('../../common.js');

var handler = module.exports = {};

handler.onGet = function(req, res) {
  var query = common.parseUrl(req).query;
  if (query.isAllDataLoad === 'true') {
    common.jsonRes(req, res, '/all');
  } else {
    if (query.pageNo === '1' && query.pageSize === '10') {
      common.jsonRes(req, res, '/page_1');
    }
    else if (query.pageNo === '2' && query.pageSize === '10') {
      common.jsonRes(req, res, '/page_2');
    } else {
      common.jsonRes(req, res, '/all');
    }
  }
};

handler.onPost = function(req, res, data) {
  setTimeout(function(){
    if (data.buildingName === '1号楼A座') {
      res.setHeader('Content-Type', 'application/json');
      res.write(JSON.stringify({ errMsg: '楼宇重复！' }));
    }
    res.statusCode = 200;
    res.end();
  }, 100);
};

handler.onPut = function(req, res, data) {
  setTimeout(function(){
    if (data.buildingName === '1号楼A座') {
      res.setHeader('Content-Type', 'application/json');
      res.write(JSON.stringify({ errMsg: '楼宇重复！' }));
    }
    res.statusCode = 200;
    res.end();
  }, 100);
};

handler.onDelete = function(req, res) {
  var query = common.parseUrl(req).query;
  if (query.ids.split(',').indexOf('120702') >= 0) {
    res.setHeader('Content-Type', 'application/json');
    res.write(JSON.stringify({ errMsg: '选中的数据不能删除！' }));
  }
  res.statusCode = 200;
  res.end();
};
