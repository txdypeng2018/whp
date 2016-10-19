(function(app) {
  'use strict';

  var medicalGuideCtrl = function($scope, $http, toastService) {
    //取得就医指南信息
    var getMedicalGuide = function() {
      $http.get('/medicalGuide').success(function(data) {
        $scope.medicalGuide = data;
      }).error(function(data){
        toastService.show(data);
      });
    };

    $scope.$on('$ionicView.beforeEnter', function(){
      if (angular.isUndefined($scope.medicalGuide) || $scope.medicalGuide === '') {
        getMedicalGuide();
      }
    });
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('medicalGuide', {
      url: '/medicalGuide',
      templateUrl: 'modules/medicalGuide/medicalGuide.html',
      controller: medicalGuideCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
