(function(app) {
  'use strict';

  var feedbackViewCtrl = function($scope, $http, $stateParams, $cordovaToast) {
    //取得意见详细
    $http.get('/service/userOpinion/'+$stateParams.opinionId).success(function(data) {
      $scope.feedback = data;
    }).error(function(data){
      $cordovaToast.showShortBottom(data);
    });
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('settingFeedbackView', {
      url: '/setting/feedbackView/:opinionId',
      templateUrl: 'modules/setting/feedbackView.html',
      controller: feedbackViewCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
