'use strict';

var common = require('../../common.js');

var handler = module.exports = {};

handler.onGet = function(req, res) {
  var query = common.parseUrl(req).query;
  if (Number(query.pageNo) === 5) {
    setTimeout(function(){
      common.jsonRes(req, res, '/all_2');
    }, 2000);
  }
  else {
    setTimeout(function(){
      common.jsonRes(req, res, '/all_1');
    }, 2000);
  }
};
