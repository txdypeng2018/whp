(function(app) {
  'use strict';

  var registrationDateCtrl = function($scope, $window, $state) {
    $scope.daySelected = {
      select1 : false,
      select2 : false,
      select3 : false
    };
    $scope.dayClk = function(index) {
      for (var key in $scope.daySelected) {
        if (key !== ('select'+index)) {
          $scope.daySelected[key] = false;
        }
      }
      $scope.daySelected['select'+index] = !$scope.daySelected['select'+index];
    };

    $scope.dateSelect = function(id) {
      $state.go('mainRegistrationDoctorY', {id: id});
    };

    $scope.$watch('$viewContentLoaded', function() {
      document.getElementById('registrationDate_doctor').style.height =
        (document.getElementById('registrationDate_content').offsetHeight - 75) + 'px';
    });
    angular.element($window).bind('resize', function() {
      document.getElementById('registrationDate_doctor').style.height =
        (document.getElementById('registrationDate_content').offsetHeight - 75) + 'px';
    });
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('mainRegistrationDate', {
      url: '/main/registrationDate/:id',
      templateUrl: 'modules/main/registration/registrationDate.html',
      controller: registrationDateCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
