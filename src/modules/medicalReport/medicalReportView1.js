(function(app) {
  'use strict';

  var medicalReportView1Ctrl = function($scope, $http, $stateParams, toastService) {
    //取得报告详细
    $http.get('/medicalReports/'+$stateParams.category+'/'+$stateParams.id).success(function(data) {
      $scope.report = data;
    }).error(function(data){
      toastService.show(data);
    });
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('medicalReportView1', {
      url: '/medicalReportList/medicalReportView1/:category/:id',
      templateUrl: 'modules/medicalReport/medicalReportView1.html',
      controller: medicalReportView1Ctrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
