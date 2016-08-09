(function(app) {
  'use strict';

  var familyMemberEditCtrl = function() {

  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('familyMemberEdit', {
      url: '/familyMember/familyMemberEdit',
      templateUrl: 'modules/familyMember/familyMemberEdit.html',
      controller: familyMemberEditCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
