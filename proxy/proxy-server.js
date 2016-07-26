'use strict';

var http = require('http');
var common = require('./modules/common.js');

console.log('');
console.log('PEP Proxy Server');
console.log('');

var onReq = function(req, res) {
  var query = common.parseUrl(req).query;

  console.log('Received %s request %s', req.method, req.url);
  //console.log('with header %j', req.rawHeaders);
  if (req.method === 'GET' || req.method === 'DELETE') {
    console.log('With query %j', query);
  } else {
    common.parsePostData(req, res, function(req, res, data) {
      console.log('with data %j', data);
    });
  }

  var handler = {}, curPath = '';
  try {
    curPath = common.url2path(req, '.');
    handler = require(curPath + '/handler.js');
  } catch (err) {
    console.log('Could not find handler in \'%s\'.', curPath);
    try {
      curPath = common.url2path(req, '.', 1);
      handler = require(curPath + '/handler.js');
      console.log('Find handler in its parent folder \'%s\'.', curPath);
    } catch (error) {
      console.log('Get handler error %j, fallback to default handler.', error);
    }
  }
  handler.onPost = handler.onPost ? handler.onPost : common.commonPost;
  handler.onPut = handler.onPut ? handler.onPut : common.commonPost;
  handler.onGet = handler.onGet ? handler.onGet : common.commonGet;
  handler.onDelete = handler.onDelete ? handler.onDelete : common.commonGet;

  if (req.method === 'POST') {
    common.parsePostData(req, res, handler.onPost);
  } else if (req.method === 'PUT') {
    common.parsePostData(req, res, handler.onPut);
  } else if (req.method === 'GET') {
    handler.onGet(req, res);
  } else if (req.method === 'DELETE') {
    handler.onDelete(req, res);
  } else {
    res.statusCode = 401;
    res.end();
  }
};

http.createServer(onReq).listen(9090);

