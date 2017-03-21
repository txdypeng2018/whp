'use strict';

var common = require('../../common.js');

var handler = module.exports = {};

handler.onGet = function(req, res) {
  var query = common.parseUrl(req).query;
  if (query.viewTypeId === '1') {
    common.jsonRes(req, res, '/all_1');
  }
  else if (query.viewTypeId === '2') {
    common.jsonRes(req, res, '/all_2');
  }
  else {
    if (query.id === '1') {
      common.jsonRes(req, res, '/all_0');
    } else if (query.id === '2') {
      common.jsonRes(req, res, '/all_1');
    }
    else {
      common.jsonRes(req, res, '/all_2');
    }
  }
};

handler.onPost = function(req, res, data) {
  if (data.patientId === '1') {
    res.writeHeader(200, {'Content-Type': 'application/json'});
    res.write(JSON.stringify({ orderNum: '1' ,registrationId: '3'}));
    res.end();
  }
  else {
    res.writeHeader(400, {'Content-Type': 'text/plain'});
    res.end('已经存在挂号单');
  }
};

handler.onPut = function(req, res, data) {
  console.info(data);
  res.writeHeader(200, {'Content-Type': 'text/plain'});
  res.end('已经存在挂号单');
};
