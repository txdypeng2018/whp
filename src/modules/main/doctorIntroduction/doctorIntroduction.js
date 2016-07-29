(function(app) {
  'use strict';

  var doctorIntroductionCtrl = function($scope, $http, $state) {
    var getDoctorIntroductions = function(param) {
      $http.get('/main/doctorIntroduction', {params: param}).success(function(data) {
        $scope.introductions = data;
      });
    };

    $scope.placeholderClk = function() {
      setTimeout(function() {
        document.getElementById('doctorIntroduction_search').focus();
      }, 100);
    };
    $scope.doSearch = function() {
      getDoctorIntroductions({searchName: $scope.searchName});
    };
    getDoctorIntroductions({searchName: $scope.searchName});

    $scope.viewIntroduction = function(id) {
      $state.go('mainDoctorIntroductionView', {id: id});
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('mainDoctorIntroduction', {
      url: '/main/doctorIntroduction',
      templateUrl: 'modules/main/doctorIntroduction/doctorIntroduction.html',
      controller: doctorIntroductionCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
