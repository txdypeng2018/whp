(function(app) {
  'use strict';

  var registerConfirmTodayCtrl = function($scope, $http, $state, $stateParams, $ionicPopup, $timeout, $filter) {
    var doctorId = $stateParams.doctorId;
    var deptId = $stateParams.deptId;
    var today = $filter('date')(new Date(),'yyyy-MM-dd');
    $scope.todayDisplay = $filter('date')(new Date(),'yyyy年MM月dd日');

    //取得患者信息
    $http.get('/patients/patient').success(function(data) {
      $scope.patient = data;
    });
    //取得医生信息
    $http.get('/register/doctor', {params: {id: doctorId, date: today, deptId: $stateParams.deptId}}).success(function(data) {
      $scope.doctor = data;
    });
    //取得科室信息
    $http.get('/organization/depts/'+deptId).success(function(data) {
      $scope.dept = data;
    });
    //取得温馨提示信息
    $http.get('/register/todayPrompt').success(function(data) {
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
        isAppointment: 0,
        registerDate: today,
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
    $stateProvider.state('registerConfirmToday', {
      url: '/register/registerConfirmToday/:doctorId/:deptId',
      cache: 'false',
      templateUrl: 'modules/register/registerConfirmToday.html',
      controller: registerConfirmTodayCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
