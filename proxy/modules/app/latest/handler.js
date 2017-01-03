/**
 * Created by Administrator on 2016/12/14.
 */
'use strict';

var common = require('../../common.js');

var handler = module.exports = {};

handler.onGet = function(req, res) {
  common.jsonRes(req, res, '/all');
};
