'use strict';

var common = require('../../../../common.js');

var handler = module.exports = {};

handler.onGet = function(req, res) {
  var query = common.parseUrl(req).query;
  if (query.personId === '1') {
    common.jsonRes(req, res, '/certificate_1');
  }
  else if (query.personId === '2') {
    common.jsonRes(req, res, '/certificate_2');
  }
  else {
    common.jsonRes(req, res, '/empty');
  }
};
