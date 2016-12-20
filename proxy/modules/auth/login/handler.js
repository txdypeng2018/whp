'use strict';

var handler = module.exports = {};

handler.onPost = function(req, res, data) {
  var username = data.username;
  if (username === 'admin') {
    res.statusCode = 200;
    res.write('eyJpZCI6ImRjNjU3NjZjLTAxNzYtNGExZS1hZDBlLWRkMDZiYTY0NWM3bCIsIm5hbWUiOiJhZG1pbiJ9.eyJlbXBOYW1lIjpudWxsLCJyb2xlcyI6ImEsYixjIn0.jgFLzfaFjADFUfti3jGMNqhYb1KN1anU8OkXKh3uKwk');
    res.end();
  } else {
    res.statusCode = 401;
    res.end();
  }
};
