(function(app) {
  'use strict';

  var tabRegistrationCtrl = function($scope, $state, $stateParams, $http) {
    //取得挂号单
    var registrationList = function(memberId) {
      $http.get('/register/registrations', {params: {memberId: memberId}}).success(function(data) {
        $scope.registrations = data;
      });
    };

    //取得登录的患者信息
    var getPatientInfo = function(memberId) {
      $http.get('/patients/patient', {params: {memberId: memberId}}).success(function(data) {
        $scope.patient = data;
        registrationList(memberId);
      });
    };
    getPatientInfo($stateParams.memberId);

    //选择家庭成员
    $scope.selectMember = function() {
      $state.go('familyMemberSelect', {skipId: 'tab.registration', memberId: $stateParams.memberId});
    };

    //挂号单选择
    $scope.registrationClk = function(id) {
      $state.go('registrationView', {registrationId: id});
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('tab.registration', {
      url: '/registration/:memberId',
      cache: 'false',
      views:{
        'tab-registration':{
          templateUrl: 'modules/tab/registration/registration.html',
          controller: tabRegistrationCtrl
        }
      }
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
