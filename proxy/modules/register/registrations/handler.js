/**
 * Created by Administrator on 2016/12/1.
 */
'use strict';

var common = require('../../common.js');

var handler = module.exports = {};

handler.onGet = function(req, res) {
    var query = common.parseUrl(req).query;
    if (query.subjectId === '2') {
      common.jsonRes(req, res, '/sub_2');
    }
    else if (query.subjectId === '3') {
      common.jsonRes(req, res, '/sub_3');
    }
    else if (query.subjectId === '4') {
      common.jsonRes(req, res, '/sub_4');
    }
    else {
      common.jsonRes(req, res, '/all');
    }
};
