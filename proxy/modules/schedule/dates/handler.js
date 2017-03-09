'use strict';

var common = require('../../common.js');

var handler = module.exports = {};

handler.onGet = function(req, res) {
  var query = common.parseUrl(req).query;
  if (typeof(query.date) === 'undefined'|| query.date === '') {
    common.jsonRes(req, res, '/all_1');
  }
  else {
    common.jsonRes(req, res, '/all_2');
  }
};
