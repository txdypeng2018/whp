(function(app) {
  'use strict';

  var messageCtrl = function() {

  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('tab.message', {
      url: '/message',
      views:{
        'tab-message':{
          templateUrl: 'modules/message/message.html',
          controller: messageCtrl
        }
      }
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
