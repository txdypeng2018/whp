'use strict';

var common = require('../../common.js');

var handler = module.exports = {};

handler.onGet = function(req, res) {
  var query = common.parseUrl(req).query;
  if (query.floorParentId === '120701') {
    common.jsonRes(req, res, '/nanhu1a');
    if (query.isAllDataLoad === 'true') {
      common.jsonRes(req, res, '/nanhu1a');
    } else {
      if (query.pageNo === '1' && query.pageSize === '20') {
        common.jsonRes(req, res, '/nanhu1a_1');
      }
      else if (query.pageNo === '2' && query.pageSize === '20') {
        common.jsonRes(req, res, '/nanhu1a_2');
      } else {
        common.jsonRes(req, res, '/nanhu1a');
      }
    }
  } else {
    common.jsonRes(req, res, '/empty');
  }
};

handler.onPost = function(req, res, data) {
  setTimeout(function(){
    if (data.floorId === '12070101') {
      res.setHeader('Content-Type', 'application/json');
      res.write(JSON.stringify({ errMsg: '楼层重复！' }));
    }
    res.statusCode = 200;
    res.end();
  }, 100);
};

handler.onPut = function(req, res, data) {
  setTimeout(function(){
    if (data.floorName === 'B1F') {
      res.setHeader('Content-Type', 'application/json');
      res.write(JSON.stringify({ errMsg: '楼层重复！' }));
    }
    res.statusCode = 200;
    res.end();
  }, 100);
};

handler.onDelete = function(req, res) {
  var query = common.parseUrl(req).query;
  if (query.ids.split(',').indexOf('12070102') >= 0) {
    res.setHeader('Content-Type', 'application/json');
    res.write(JSON.stringify({ errMsg: '选中的数据不能删除！' }));
  }
  res.statusCode = 200;
  res.end();
};
