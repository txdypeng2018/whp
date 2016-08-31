(function(app) {
  'use strict';

  var forgetPasswordCtrl = function($scope, $state, $ionicHistory, $http, $cordovaToast) {
    $scope.$on('$ionicView.beforeEnter', function(){
      $scope.isSubmit = false;
      $scope.input = {
        phone: ''
      };
    });

    $scope.getCode = function(){
      var param = {
        category: '2',
        phone: $scope.input.phone.toString()
      };
      $scope.isSubmit = true;
      $http.get('/permission/verificationCode', {params: param}).success(function() {
        $state.go('forgetPasswordSetting', {phone: param.phone});
      }).error(function(data){
        $scope.isSubmit = false;
        $cordovaToast.showShortBottom(data);
      });
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('forgetPassword', {
      url: '/forgetPassword',
      templateUrl: 'modules/login/forgetPassword.html',
      controller: forgetPasswordCtrl
    });
  };

    app.config(mainRouter);
})(angular.module('isj'));


