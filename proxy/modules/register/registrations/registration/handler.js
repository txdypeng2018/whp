'use strict';

var common = require('../../../common.js');

var handler = module.exports = {};

handler.onGet = function(req, res) {
  common.jsonRes(req, res, '/all');
};

handler.onPost = function(req, res, data) {
  if (data.patientId === '1') {
    res.writeHeader(200, {'Content-Type': 'application/json'});
    res.write(JSON.stringify({ orderNum: '1' }));
    res.end();
  }
  else {
    res.writeHeader(400, {'Content-Type': 'text/plain'});
    res.end('已经存在挂号单');
  }
};
