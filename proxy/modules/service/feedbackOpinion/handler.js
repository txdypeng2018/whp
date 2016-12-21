'use strict';

var common = require('../../common.js');

var handler = module.exports = {};

handler.onGet = function(req, res) {
  var query = common.parseUrl(req).query;
  if (query.isAllDataLoad === 'true') {
    common.jsonRes(req, res, '/all');
  }
  else {
    if (query.pageNo === '1') {
      common.jsonRes(req, res, '/page_1');
    }
    else if (query.pageNo === '2') {
      common.jsonRes(req, res, '/page_2');
    }
  }
};

handler.onPut = function(req, res, data) {
  setTimeout(function(){
    if (data.feedback === '') {
      res.setHeader('Content-Type', 'application/json');
      res.write(JSON.stringify({ errMsg: '填写的反馈意见为空！' }));
    }
    res.statusCode = 200;
    res.end();
  }, 100);
};
