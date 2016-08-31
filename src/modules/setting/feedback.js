(function(app) {
  'use strict';

  var feedbackCtrl = function($scope, $http, $ionicHistory, $cordovaToast) {
    $scope.$on('$ionicView.beforeEnter', function(){
      $scope.isSubmit = false;
      $scope.input = {
        opinion: ''
      };
    });

    $scope.opinionSubmit = function() {
      if ($scope.input.opinion === '') {
        $cordovaToast.showShortBottom('请填写反馈内容');
      }
      else {
        $scope.isSubmit = true;
        $http.post('/service/userOpinion', {opinion: $scope.input.opinion}).success(function() {
          $ionicHistory.goBack();
          $cordovaToast.showShortBottom('意见提交成功，谢谢您的支持');
        }).error(function(data){
          $scope.isSubmit = false;
          $cordovaToast.showShortBottom(data);
        });
      }
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('settingFeedback', {
      url: '/setting/feedback',
      templateUrl: 'modules/setting/feedback.html',
      controller: feedbackCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
