(function(app) {
  'use strict';

  var paySelectCtrl = function($scope) {
    $scope.paySelectValue = '';
    $scope.paySelect = function(value) {
      $scope.paySelectValue = value;
      angular.element(document.querySelectorAll('.select-yes')).addClass('select-none');
      angular.element(document.getElementById('select_yes_'+value)).removeClass('select-none');
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('mainPaySelect', {
      url: '/main/paySelect',
      templateUrl: 'modules/main/registration/paySelect.html',
      controller: paySelectCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
