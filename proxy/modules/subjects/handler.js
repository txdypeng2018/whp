'use strict';

var common = require('../common.js');

var handler = module.exports = {};

handler.onGet = function(req, res) {
  var query = common.parseUrl(req).query;
  if (query.subjectId === null || query.subjectId === '') {
    common.jsonRes(req, res, '/oneLevel');
  }
  else if (query.subjectId === '1') {
    common.jsonRes(req, res, '/twoLevel_1');
  }
};
