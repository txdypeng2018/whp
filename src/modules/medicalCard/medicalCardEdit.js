(function(app) {
  'use strict';

  var medicalCardEditCtrl = function() {

  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('medicalCardEdit', {
      url: '/medicalCard/medicalCardEdit',
      templateUrl: 'modules/medicalCard/medicalCardEdit.html',
      controller: medicalCardEditCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
