'use strict';

var handler = module.exports = {};

handler.onGet = function(req, res) {
  var resBody = '' +
    '<script>' +
    '  setTimeout("window.location = \'../#/workflow/designer\'", 2000);' +
    '</script>' +
    '<span>' +
    '  This is the mock workflow web designer, and will go back to model list page.' +
    '</span>';

  res.statusCode = 200;
  res.setHeader('Content-Type', 'text/html');
  res.write(resBody);
  res.end();
};
