(function(app) {
  'use strict';

  var registerConfirmTodayCtrl = function($scope, $http, $state, $stateParams, $ionicPopup, $timeout, $filter, $ionicHistory) {
    var doctorId = $stateParams.doctorId;
    var today = $filter('date')(new Date(),'yyyy-MM-dd');
    $scope.todayDisplay = $filter('date')(new Date(),'yyyy年MM月dd日');

    //取得挂号须知
    $http.get('/register/agreement').success(function(data) {
      $scope.agreement = data;
      if (angular.isUndefined($stateParams.memberId) || $stateParams.memberId === '') {
        $scope.showAgreement();
      }
    });
    //取得登录账号就医人
    var getPatient = function() {
      $http.get('/patients/patient', {params: {memberId: $stateParams.memberId}}).success(function(data) {
        $scope.patient = data;
      });
    };
    getPatient();
    //取得关系类别
    $http.get('/dataBase/familyMenberTypes').success(function(data) {
      $scope.memberTypes = data;
    });
    //取得医生信息
    $http.get('/register/doctor', {params: {id: doctorId, date: today}}).success(function(data) {
      $scope.doctor = data;
    });
    //取得温馨提示信息
    $http.get('/register/todayPrompt').success(function(data) {
      $scope.prompt = data;
    });

    //返回上页
    $scope.goBack = function() {
      $ionicHistory.goBack();
    };

    //挂号须知提示框
    $scope.showAgreement = function() {
      var myPopup = $ionicPopup.show({
        template: '<div style="padding: 3px;font-size:15px">'+$scope.agreement+'</div>',
        title: '挂号须知',
        buttons: [
          {
            text: '确定',
            type: 'positive',
            onTap: function(e) {
              e.preventDefault();
              myPopup.close();
            }
          }
        ]
      });
      $timeout(function() {
        angular.element(document.querySelector('.popup')).css('width','80%');
        angular.element(document.querySelector('.popup-title')).css('font-size','18px');
      });
    };

    //选择家庭成员
    $scope.selectMember = function() {
      $state.go('familyMemberSelect', {skipId: 'registerConfirmToday', doctorId: doctorId, memberId: $scope.patient.id});
    };

    //挂号确认事件
    $scope.registerConfirm = function() {
      //生成挂号单
      var registration = {
        patientId: $scope.patient.id,
        isAppointment: 0,
        registerDate: today,
        deptId: $scope.doctor.deptId,
        doctorId: doctorId,
        clinicCategoryCode: $scope.doctor.clinicCategoryCode,
        amount: $scope.doctor.amount,
        payStatus: '0'
      };
      $http.put('/register/registrations/registration', registration).success(function(data) {
        if (angular.isUndefined(data.errMsg)) {
          $state.go('paymentSelect', {orderNum: data.orderNum});
        }
      });
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('registerConfirmToday', {
      url: '/register/registerConfirmToday/:doctorId/:memberId',
      templateUrl: 'modules/register/registerConfirmToday.html',
      controller: registerConfirmTodayCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
