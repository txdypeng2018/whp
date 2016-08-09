(function(app) {
  'use strict';

  var feedbackCtrl = function() {

  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('settingFeedback', {
      url: '/setting/feedback',
      cache: 'false',
      templateUrl: 'modules/setting/feedback.html',
      controller: feedbackCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
