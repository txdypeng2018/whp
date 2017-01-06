'use strict';

var common = require('../../../../common.js');

var handler = module.exports = {};

handler.onGet = function(req, res) {
  var query = common.parseUrl(req).query;
  if (query.personId === '1' && query.certificateId === '1') {
    common.jsonRes(req, res, '/loan_1_1');
  }
  else {
    common.jsonRes(req, res, '/empty');
  }
};
