'use strict';

var handler = module.exports = {};

/*handler.onGet = function(req, res) {
  common.jsonRes(req, res, '/all');
};*/

handler.onPut = function(req, res, data) {

  //console.info(data.phone);
  res.setHeader('Content-Type', 'application/json');
  if(data.phone === '123'){
  	if(data.password === '123'){
  		res.write(JSON.stringify({ status: 'success' }));
  	}else{
  		res.write(JSON.stringify({ status: 'fail' }));
  	}
	
  }	else{
  	res.write(JSON.stringify({ status: 'fail' }));
  }
  
  res.statusCode = 200;
  res.end();
};
