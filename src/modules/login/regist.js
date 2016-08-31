(function(app) {
  'use strict';

  var registCtrl = function($scope,$state,$ionicHistory,$http,$cordovaToast) {
    $scope.$on('$ionicView.beforeEnter', function(){
      $scope.isSubmit = false;
      $scope.input = {
        phone: ''
      };
    });

    $scope.getCode = function(){
      var param = {
        category: '1',
        phone: $scope.input.phone.toString()
      };
      $scope.isSubmit = true;
      $http.get('/permission/verificationCode', {params: param}).success(function() {
        $state.go('registSetting', {phone: param.phone});
      }).error(function(data){
        $scope.isSubmit = false;
        $cordovaToast.showShortBottom(data);
      });
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('regist', {
      url: '/regist',
      templateUrl: 'modules/login/regist.html',
      controller: registCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));


