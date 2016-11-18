'use strict';

var common = require('../../../../common.js');

var handler = module.exports = {};

handler.onGet = function(req, res) {
  var query = common.parseUrl(req).query;
  if (query.typeCode === 'GB-T2261') {
    common.jsonRes(req, res, '/sex');
  }
  else if (query.typeCode === 'GB/T 2659-2000') {
    common.jsonRes(req, res, '/country');
  }
  else if (query.typeCode === 'ZDY 281-2011') {
    common.jsonRes(req, res, '/urgent');
  }
  else {
    common.jsonRes(req, res, '/empty');
  }
};
