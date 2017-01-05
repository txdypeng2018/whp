'use strict';

var handler = module.exports = {};

handler.onPost = function(req, res, data) {
  if (data.phone === '123' && data.password === '123') {
    res.writeHeader(200, {'Content-Type': 'text/plain'});
    res.end('1234567890');
  }
  else {
    res.writeHeader(400, {'Content-Type': 'text/plain'});
    res.end('用户名或密码错误');
  }
};
