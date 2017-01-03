'use strict';

var handler = module.exports = {};

handler.onPost = function(req, res, data) {
  console.info(data);
  res.writeHeader(200, {'Content-Type': 'text/plain'});
  res.end('1234567890');
};

handler.onPut = function(req, res, data) {
  console.info(data);
  res.writeHeader(200, {'Content-Type': 'text/plain'});
  res.end('');
};
