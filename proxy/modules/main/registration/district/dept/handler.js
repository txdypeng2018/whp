'use strict';

var common = require('../../../../common.js');

var handler = module.exports = {};

handler.onGet = function(req, res) {
  var query = common.parseUrl(req).query;
  if (query.id === '1') {
    common.jsonRes(req, res, '/dept_1');
  }
  else if (query.id === '2') {
      common.jsonRes(req, res, '/dept_2');
  }
  else if (query.id === '3') {
    common.jsonRes(req, res, '/dept_3');
  }
};
