'use strict';

var common = require('../../../common.js');

var handler = module.exports = {};

handler.onGet = function(req, res) {
  var query = common.parseUrl(req).query;
  if (typeof(query.memberId) === 'undefined' || query.memberId === null || query.memberId === '' || query.memberId === '1') {
    common.jsonRes(req, res, '/all_me');
  }
  else {
    common.jsonRes(req, res, '/all_other');
  }
};

handler.onPost = function(req, res, data) {
  console.info(data);
  res.writeHeader(200, {'Content-Type': 'text/plain'});
  res.end('');
};

handler.onPut = function(req, res, data) {
  console.info(data);
  res.writeHeader(200, {'Content-Type': 'text/plain'});
  res.end('');
};

handler.onDelete = function(req, res) {
  res.writeHeader(200, {'Content-Type': 'text/plain'});
  res.end('');
};
