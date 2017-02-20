/**
 * Created by Administrator on 2017/2/7.
 */
'use strict';

var common = require('../../common.js');

var handler = module.exports = {};

handler.onGet = function(req, res) {
    var query = common.parseUrl(req).query;
    if (query.query === '1b858cb1-3de1-43c6-a95a-fc72c95a1f7c') {
        common.jsonRes(req, res, '/page_1');
    }else if(query.query === '1b858cb1-3de1-43c6-a95a-fc72c95a1f7d'){
        common.jsonRes(req, res, '/page_2');
    }
};