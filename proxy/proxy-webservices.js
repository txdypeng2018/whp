'use strict';

var http = require('http');
var soap = require('soap');

console.log('');
console.log('PEP WebServices Proxy Server');
console.log('');

var server = http.createServer(function(request,response) {
  response.end("404: Not Found: " + request.url);
}).listen(8080);

var serverLog = function(type, data) {
  console.log('type: ' + type);
  console.log('data: ' + data);
};

var register = function(path) {
  var handler = require('.' + path + '/handler.js');
  var xml = require('fs').readFileSync('proxy' + path + '/wsdl.wsdl', 'utf8');
  xml = xml.replace(/:address location=".*" \/>/g, ':address location="http://localhost:8080' + path + '" />');
  soap.listen(server, path, handler.service, xml).log = serverLog;
};

register('/his/neusoft');

