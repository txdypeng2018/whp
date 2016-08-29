'use strict';

var common = require('../../common.js');

var handler = module.exports = {};

handler.onGet = function(req, res) {
  var path = common.parseUrl(req).path;
  if (path.substring(path.length-5, path.length) === 'count') {
    common.jsonRes(req, res, '/count');
  }
  else {
    common.jsonRes(req, res, '/all');
  }
};

handler.onPost = function(req, res, data) {
  console.info(data);
  res.writeHeader(200, {'Content-Type': 'text/plain'});
  res.end('');
};

handler.onDelete = function(req, res) {
  res.writeHeader(200, {'Content-Type': 'text/plain'});
  res.end('');
};
