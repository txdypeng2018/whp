(function(app) {
  'use strict';

  var hospitalNavigationCtrl = function($scope, $http, $state, $ionicModal) {
    $scope.contacts = [
      { name: 'Gordon Freeman' },
      { name: 'Barney Calhoun' },
      { name: 'Lamarr the Headcrab' }
    ];

    $ionicModal.fromTemplateUrl('modules/main/hospitalNavigation/buildingList.html', {
      scope: $scope,
      animation: 'slide-in-up'
    }).then(function(modal) {
      $scope.modal = modal;
    });
    $scope.createContact = function(u) {
      $scope.contacts.push({ name: u.firstName + ' ' + u.lastName });
      $scope.modal.hide();
    };

    $scope.buildingClk = function() {
      $scope.modal.hide();
      $state.go('mainFloorList');
    };

    $scope.districtClk = function(event, id) {
      var districtDocuments = document.querySelectorAll('.district-button');
      for (var i = 0 ; i < districtDocuments.length ; i++) {
        if (!angular.element(districtDocuments[i]).hasClass('button-outline')) {
          angular.element(districtDocuments[i]).addClass('button-outline');
        }
      }
      angular.element(event.currentTarget).removeClass('button-outline');
    };

    $http.get('/main/registration/district').success(function(data) {
      $scope.districts = data;
    });
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('mainHospitalNavigation', {
      url: '/main/hospitalNavigation',
      templateUrl: 'modules/main/hospitalNavigation/hospitalNavigation.html',
      controller: hospitalNavigationCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
