'use strict';

var common = require('../../../common.js');

var handler = module.exports = {};

handler.onGet = function(req, res) {
  var query = common.parseUrl(req).query;
  if (query.name !== undefined && query.medicalNum !== undefined && query.phoneOrIdcard !== undefined) {
    common.jsonRes(req, res, '/all');
  }
};