(function(app) {
  'use strict';

  var registrationViewCtrl = function($scope, $http, $state, $stateParams, $filter, $ionicPopup, $ionicHistory, $cordovaToast) {
    $scope.$on('$ionicView.beforeEnter', function(){
      $scope.isToday = true;
      $scope.registration = {};
      $scope.patient = {};
      //取得就诊人
      $http.get('/user/familyMembers/familyMember', {params: {memberId: $stateParams.memberId}}).success(function(data) {
        $scope.patient = data;
      }).error(function(data){
        $cordovaToast.showShortBottom(data);
      });
      //家庭关系类别
      $http.get('/dataBase/familyMenberTypes').success(function(data) {
        $scope.memberTypes = data;
      }).error(function(data){
        $scope.memberTypes = {};
        $cordovaToast.showShortBottom(data);
      });
      //取得挂号单
      $http.get('/register/registrations/registration', {params: {id: $stateParams.registrationId}}).success(function(data) {
        $scope.registration = data;
        if ($scope.registration.registerDate.length <= 11) {
          $scope.visitTime = '请到分诊台咨询';
        }
        else {
          $scope.visitTime = $scope.registration.registerDate;
        }
        var today = $filter('date')(new Date(), 'yyyy年MM月dd日');
        if (today < $scope.registration.registerDate.substring(0, 11)) {
          $scope.isToday = false;
        }
      }).error(function(data){
        $cordovaToast.showShortBottom(data);
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
            $cordovaToast.showShortBottom('退号成功');
          }).error(function(data){
            $cordovaToast.showShortBottom(data);
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
