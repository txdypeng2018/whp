'use strict';

var common = require('../common.js');

var handler = module.exports = {};

handler.onGet = function(req, res) {
  var query = common.parseUrl(req).query;
  if (query.districtId === '1') {
    common.jsonRes(req, res, '/all_1');
  }
  else if (query.districtId === '2') {
    common.jsonRes(req, res, '/all_2');
  }
  else {
    common.jsonRes(req, res, '/all_3');
  }
};
