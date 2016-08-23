(function(app) {
  'use strict';

  var registerConfirmApptCtrl = function($scope, $http, $state, $stateParams, $ionicPopup, $timeout, $ionicHistory) {
    var doctorId = $stateParams.doctorId;
    var date = $stateParams.date;
    $scope.dateDisplay = date.substring(0,4)+'年'+date.substring(5,7)+'月'+date.substring(8,10)+'日'+date.substring(10,16);

    //取得挂号须知
    $http.get('/register/agreement').success(function(data) {
      $scope.agreement = data;
      if (angular.isUndefined($stateParams.memberId) || $stateParams.memberId === '') {
        $scope.showAgreement();
      }
    });
    //取得患者信息
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
    $http.get('/register/doctor', {params: {id: doctorId, date: date}}).success(function(data) {
      $scope.doctor = data;
    });
    //取得温馨提示信息
    $http.get('/register/apptPrompt').success(function(data) {
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
      $state.go('familyMemberSelect', {skipId: 'registerConfirmAppt', doctorId: doctorId, date: date, memberId: $scope.patient.id});
    };

    //挂号确认事件
    $scope.registerConfirm = function() {
      //生成挂号单
      var registration = {
        patientId: $scope.patient.id,
        isAppointment: 1,
        registerDate: date,
        deptId: $scope.doctor.deptId,
        doctorId: doctorId,
        clinicCategoryCode: $scope.doctor.clinicCategoryCode,
        amount: $scope.doctor.amount,
        payStatus: '0'
      };
      $http.put('/register/registration', registration).success(function(data) {
        if (angular.isUndefined(data.errMsg)) {
          $state.go('paymentSelect', {orderNum: data.orderNum});
        }
      });
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('registerConfirmAppt', {
      url: '/register/registerConfirmAppt/:doctorId/:date/:memberId',
      templateUrl: 'modules/register/registerConfirmAppt.html',
      controller: registerConfirmApptCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
