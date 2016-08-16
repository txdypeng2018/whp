'use strict';

var common = require('../../common.js');

var handler = module.exports = {};

handler.onGet = function(req, res) {
  var query = common.parseUrl(req).query;
  if (typeof(query.memberId) === 'undefined' || query.memberId === null || query.memberId === '') {
    common.jsonRes(req, res, '/all_me');
  }
  else {
    common.jsonRes(req, res, '/all_other');
  }
};
