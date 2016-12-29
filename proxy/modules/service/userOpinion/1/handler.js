'use strict';

var common = require('../../../../common.js');

var handler = module.exports = {};

handler.onGet = function(req, res) {
  common.jsonRes(req, res, '/all');
};

handler.onPut = function(req, res) {
  setTimeout(function(){
    res.statusCode = 200;
    res.end();
  }, 100);
};

