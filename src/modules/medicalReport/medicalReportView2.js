(function(app) {
  'use strict';

  var medicalReportView2Ctrl = function($scope, $http, $stateParams, toastService) {
    $http.get('/medicalReports/'+$stateParams.category+'/'+$stateParams.id).success(function(data) {
      $scope.image = data;
    }).error(function(data){
      toastService.show(data);
    });
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('medicalReportView2', {
      url: '/medicalReportList/medicalReportView2/:category/:id',
      templateUrl: 'modules/medicalReport/medicalReportView2.html',
      controller: medicalReportView2Ctrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
