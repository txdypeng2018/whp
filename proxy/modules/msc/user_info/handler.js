/**
 * Created by Administrator on 2017/2/7.
 */
'use strict';

var common = require('../../common.js');

var handler = module.exports = {};

handler.onGet = function(req, res) {
    var query = common.parseUrl(req).query;
    if (query.params === '李毅' || query.params === '13123456546' || query.params === '420908199003120989' || query.params ==='M005962162') {
        common.jsonRes(req, res, '/all');
    }
};