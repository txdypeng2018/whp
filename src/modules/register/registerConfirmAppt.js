(function(app) {
  'use strict';

  var registerConfirmApptCtrl = function($scope, $http, $state, $stateParams, $ionicPopup, $timeout) {
    var doctorId = $stateParams.doctorId;
    var deptId = $stateParams.deptId;
    var date = $stateParams.date;
    $scope.dateDisplay = date.substring(0,4)+'年'+date.substring(5,7)+'月'+date.substring(8,10)+'日'+date.substring(10,16);

    //取得患者信息
    $http.get('/patients/patient').success(function(data) {
      $scope.patient = data;
    });
    //取得医生信息
    $http.get('/register/doctor', {params: {id: doctorId, date: date, deptId: $stateParams.deptId}}).success(function(data) {
      $scope.doctor = data;
    });
    //取得科室信息
    $http.get('/organization/depts/'+deptId).success(function(data) {
      $scope.dept = data;
    });
    //取得温馨提示信息
    $http.get('/register/apptPrompt').success(function(data) {
      $scope.prompt = data;
    });

    //挂号须知提示框
    $scope.showAgreement = function() {
      $http.get('/register/agreement').success(function(data) {
        var myPopup = $ionicPopup.show({
          template: '<div style="padding: 3px;font-size:15px">'+data+'</div>',
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
      });
    };
    $scope.showAgreement();

    //挂号确认事件
    $scope.registerConfirm = function() {
      //生成挂号单
      var registration = {
        patientId: $scope.patient.id,
        memberCategory: '',
        isAppointment: 1,
        registerDate: date,
        deptId: deptId,
        doctorId: doctorId,
        clinicCategoryCode: $scope.doctor.clinicCategoryCode,
        amount: $scope.doctor.amount,
        payStatus: '0'
      };
      $http.put('/register/registration', registration).success(function(data) {
        if (angular.isUndefined(data.errMsg)) {
          $state.go('paymentSelect',{category: '1', id: data.id});
        }
      });
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('registerConfirmAppt', {
      url: '/register/registerConfirmAppt/:doctorId/:deptId/:date',
      cache: 'false',
      templateUrl: 'modules/register/registerConfirmAppt.html',
      controller: registerConfirmApptCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
