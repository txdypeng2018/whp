(function(app) {
  'use strict';

  var medicalReportView1Ctrl = function($scope, $http, $stateParams) {
    //取得报告详细
    $http.get('/medicalReports/'+$stateParams.category+'/'+$stateParams.id, {params: {id: $stateParams.id}}).success(function(data) {
      $scope.report = data;
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
