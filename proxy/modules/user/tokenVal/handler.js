'use strict';

var handler = module.exports = {};

handler.onGet = function(req, res) {
  res.writeHeader(200, {'Content-Type': 'text/plain'});
  res.end('');
};
