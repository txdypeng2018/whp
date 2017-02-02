'use strict';

var common = require('../../common.js');

var handler = module.exports = {};

handler.onGet = function(req, res) {
  var token = req.headers.authorization;
  if (typeof(token) !== 'undefined' && token !== null && token !== '') {
    common.jsonRes(req, res, '/all');
  }
  else {
    res.writeHeader(401, {'Content-Type': 'text/plain'});
    res.end('无权限');
  }
};
