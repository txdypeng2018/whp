'use strict';

var common = require('../../common.js');

var handler = module.exports = {};

handler.onGet = function(req, res) {
  var path = common.parseUrl(req).path;
  if (path.substring(path.length-7, path.length) === 'version') {
    common.jsonRes(req, res, '/version');
  }
  else {
    var Module = require('module');
    var fs = require('fs');
    Module._extensions['.png'] = function(module, fn) {
      var base64 = fs.readFileSync(fn).toString('base64');
      module._compile('module.exports="data:image/png;base64,' + base64 + '"', fn);
    };
    var img1 = require('./ad1.png');
    var img2 = require('./ad2.png');
    var img3 = require('./ad3.png');
    res.writeHead(200, {'Content-Type': 'application/json'});
    res.write(JSON.stringify([{
      name: '预约挂号',
      img: img1
    },{
      name: '在线缴费',
      img: img2
    },{
      name: '查看报告',
      img: img3
    }]));
    res.end();
  }
};
