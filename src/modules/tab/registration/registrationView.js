(function(app) {
  'use strict';

  var registrationViewCtrl = function($scope, $http, $state, $stateParams, $ionicPopup, $ionicHistory, toastService) {
    $scope.$on('$ionicView.beforeEnter', function(){
      $scope.registration = {};
      $scope.patient = {};
      //取得就诊人
      $http.get('/user/familyMembers/familyMember', {params: {memberId: $stateParams.memberId}}).success(function(data) {
        $scope.patient = data;
      }).error(function(data){
        toastService.show(data);
      });
      //家庭关系类别
      $http.get('/dataBase/familyMenberTypes').success(function(data) {
        $scope.memberTypes = data;
      }).error(function(data){
        $scope.memberTypes = {};
        toastService.show(data);
      });
      //取得挂号单
      $http.get('/register/registrations/registration', {params: {id: $stateParams.registrationId}}).success(function(data) {
        $scope.registration = data;
        if ($scope.registration.apptDate.length <= 11) {
          $scope.visitTime = '请到分诊台咨询';
        }
        else {
          $scope.visitTime = $scope.registration.apptDate;
        }
        if ($scope.registration.district.length > 2) {
          $scope.registration.district = $scope.registration.district.substring(0,2);
        }
      }).error(function(data){
        toastService.show(data);
      });
    });
    $scope.$on('$ionicView.beforeLeave', function(){
      if (confirmPopup !== null) {
        confirmPopup.close();
      }
    });

    //就诊、退号说明
    $scope.illustrationClk = function() {
      $state.go('registrationIllustration');
    };

    //支付
    $scope.registrationPay = function() {
      $state.go('paymentSelect', {orderNum: $scope.registration.orderNum});
    };

    //退号
    var confirmPopup = null;
    $scope.registrationBack = function() {
      confirmPopup = $ionicPopup.confirm({
        title: '提示',
        template: '您确定要退号吗?',
        cssClass: 'confirm-popup',
        cancelText: '取消',
        okText: '确定'
      });
      confirmPopup.then(function(res) {
        if(res) {
          $http.put('/register/registrations/registration/back', {registrationId: $scope.registration.id}).success(function() {
            $ionicHistory.goBack();
            toastService.show('退号成功');
          }).error(function(data){
            toastService.show(data);
          });
        }
      });
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('registrationView', {
      url: '/registration/registrationView/:registrationId/:memberId',
      templateUrl: 'modules/tab/registration/registrationView.html',
      controller: registrationViewCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
