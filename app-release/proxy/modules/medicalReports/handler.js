'use strict';

var common = require('../common.js');

var handler = module.exports = {};

handler.onGet = function(req, res) {
  var query = common.parseUrl(req).query;
  if (query.category === '1') {
    common.jsonRes(req, res, '/all_1');
  }
  else if (query.category === '2') {
    common.jsonRes(req, res, '/all_2');
  }
  else {
    common.jsonRes(req, res, '/all');
  }
};
