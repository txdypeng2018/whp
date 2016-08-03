(function(app) {
  'use strict';

  var registrationDateListCtrl = function($scope, $window, $state, ionicDatePicker) {
    $scope.daySelected = {
      select_1 : false,
      select_2 : false,
      select_3 : false,
      select_4 : false,
      select_5 : false,
      select_6 : false,
      select_7 : false
    };
    $scope.dayClk = function(index) {
      for (var key in $scope.daySelected) {
        if (key !== ('select_'+index)) {
          $scope.daySelected[key] = false;
        }
      }
      $scope.daySelected['select_'+index] = !$scope.daySelected['select_'+index];
    };

    var ipObj1 = {
      callback: function (val) {
        console.log('Return value from the datepicker popup is : ' + val, new Date(val));
      },
      disabledDates: [
        new Date(2016, 2, 16),
        new Date(2015, 3, 16),
        new Date(2015, 4, 16),
        new Date(2015, 5, 16),
        new Date('Wednesday, August 12, 2015'),
        new Date("08-16-2016"),
        new Date(1439676000000)
      ],
      from: new Date(2012, 1, 1),
      to: new Date(2016, 10, 30),
      inputDate: new Date(),
      mondayFirst: true,
      disableWeekdays: [0],
      closeOnSelect: false,
      templateType: 'popup'
    };
    $scope.dayPicker = function() {
      ionicDatePicker.openDatePicker(ipObj1);
    };

    $scope.doctorSelect = function(id) {
      $state.go('mainRegistrationDate', {id: id});
    };

    $scope.$watch('$viewContentLoaded', function() {
      document.getElementById('registrationDateList_doctor').style.height =
        (document.getElementById('registrationDateList_content').offsetHeight - 75) + 'px';
    });
    angular.element($window).bind('resize', function() {
      document.getElementById('registrationDateList_doctor').style.height =
        (document.getElementById('registrationDateList_content').offsetHeight - 75) + 'px';
    });
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('mainRegistrationDateList', {
      url: '/main/registrationDateList/:id',
      templateUrl: 'modules/main/registration/registrationDateList.html',
      controller: registrationDateListCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
