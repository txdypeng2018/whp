'use strict';

var common = require('../../common.js');

var handler = module.exports = {};

handler.onGet = function(req, res) {
  var query = common.parseUrl(req).query;
  console.log(query.query);
  if (query.query.length < 10) {
    common.jsonRes(req, res, '/user_1');
  }
  else{
    common.jsonRes(req, res, '/all');
  }
};

