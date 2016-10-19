(function(app) {
  'use strict';

  var feedbackViewCtrl = function($scope, $http, $stateParams, toastService) {
    //取得意见详细
    var getUserOpinion = function() {
      $http.get('/service/userOpinion/'+$stateParams.opinionId).success(function(data) {
        $scope.feedback = data;
      }).error(function(data){
        toastService.show(data);
      });
    };

    $scope.$on('$ionicView.beforeEnter', function(){
      if (angular.isUndefined($scope.feedback) || $scope.feedback === null) {
        getUserOpinion();
      }
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
