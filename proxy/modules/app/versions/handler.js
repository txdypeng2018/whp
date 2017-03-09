'use strict';

var handler = module.exports = {};

handler.onGet = function(req, res) {
  var data = {note: 'V3.1.0版更新内容:<br>1.开放一网通支付方式,更多支付方式,更加快捷<br>2.挂号单中支付、确认及退款状态更详细'};
  res.writeHead(200, {'Content-Type': 'application/json'});
  res.end(JSON.stringify(data));
};
