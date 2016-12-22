'use strict';

var common = require('../../common.js');

var handler = module.exports = {};

handler.onGet = function(req, res) {
  var query = common.parseUrl(req).query;
  console.log(query.query);
  if (query.query === '{userId:1}') {
    common.jsonRes(req, res, '/user_1');
  }
  else if (query.query === '{userId:1}') {
    common.jsonRes(req, res, '/user_2');
  }else{
    common.jsonRes(req, res, '/all');
  }
};

