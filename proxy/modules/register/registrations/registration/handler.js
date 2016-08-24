'use strict';

var common = require('../../../common.js');

var handler = module.exports = {};

handler.onGet = function(req, res) {
  common.jsonRes(req, res, '/all');
};

handler.onPut = function(req, res, data) {
  console.info(data);
  res.setHeader('Content-Type', 'application/json');
  res.write(JSON.stringify({ orderNum: '1' }));
  res.statusCode = 200;
  res.end();
};
