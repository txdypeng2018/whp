'use strict';

var handler = module.exports = {};

/*handler.onGet = function(req, res) {
  common.jsonRes(req, res, '/all');
};*/

handler.onPost = function(req, res, data) {
/*  console.info('idCard'+data.idCard);
  console.info('phone'+data.phone);*/
  res.setHeader('Content-Type', 'application/json');
  if(data.idCard === '13322487608'){
  		res.write(JSON.stringify({ status: 'success' }));
  }	else{
  	res.write(JSON.stringify({ status: 'fail' }));
  }
  
  res.statusCode = 200;
  res.end();
};
