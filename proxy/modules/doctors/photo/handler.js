'use strict';

var common = require('../../common.js');

var handler = module.exports = {};

handler.onGet = function(req, res) {
  var query = common.parseUrl(req).query;
  if (query.doctorId === '1' || query.doctorId === '2') {
    var Module = require('module');
    var fs = require('fs');
    Module._extensions['.png'] = function(module, fn) {
      var base64 = fs.readFileSync(fn).toString('base64');
      module._compile('module.exports="data:image/png;base64,' + base64 + '"', fn);
    };
    var image = require('./photo.png');
    res.writeHead(200, {'Content-Type': 'image/png'});
    res.write(image, 'binary');
    res.end();
  }
  else {
    res.writeHead(200, {'Content-Type': 'image/png'});
    res.write('', 'binary');
    res.end();
  }
};
