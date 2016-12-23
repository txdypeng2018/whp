'use strict';

var handler = module.exports = {};

handler.onGet = function(req, res) {
  var Module = require('module');
  var fs = require('fs');
  Module._extensions['.jpg'] = function(module, fn) {
    var base64 = fs.readFileSync(fn).toString('base64');
    module._compile('module.exports="data:image/jpg;base64,' + base64 + '"', fn);
  };
  var image = require('./1.jpg');
  res.writeHead(200, {'Content-Type': 'image/jpg'});
  res.write(image, 'binary');
  res.end();
};
