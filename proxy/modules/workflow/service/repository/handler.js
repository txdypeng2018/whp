'use strict';

var common = require('../../../common.js');

var handler = module.exports = {};

handler.onGet = function(req, res) {
  var query = common.parseUrl(req).query;
  if (query.size) {
    common.jsonRes(req, res, '/all');
  } else if (query.nameLike) {
    common.jsonRes(req, res, '/nameLike');
  } else {
    common.jsonRes(req, res, '/page');
  }
};
