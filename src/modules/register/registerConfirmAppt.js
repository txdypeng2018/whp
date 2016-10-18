(function(app) {
  'use strict';

  var registerConfirmApptCtrl = function($scope, $http, $state, $stateParams, $ionicPopup, $ionicHistory, toastService) {
    var doctorId = $stateParams.doctorId;
    var date = $stateParams.date;
    $scope.dateDisplay = date.substring(0,4)+'年'+date.substring(5,7)+'月'+date.substring(8,10)+'日'+date.substring(10,16);

    //取得挂号须知
    $http.get('/register/agreement').success(function(data) {
      $scope.agreement = data;
    }).error(function(data){
      toastService.show(data);
    });
    //取得患者信息
    var getPatient = function() {
      $http.get('/user/familyMembers/familyMember', {params: {memberId: $stateParams.memberId}}).success(function(data) {
        $scope.patient = data;
      }).error(function(data){
        toastService.show(data);
      });
    };
    getPatient();

    //若选中则代表同意规则
    $scope.isChecked = true;

    //取得关系类别
    $http.get('/dataBase/familyMenberTypes').success(function(data) {
      $scope.memberTypes = data;
    }).error(function(data){
      toastService.show(data);
    });
    //取得医生信息
    $http.get('/register/doctor', {params: {id: doctorId, date: date}}).success(function(data) {
      $scope.doctor = data;
    }).error(function(data){
      toastService.show(data);
    });
    //取得温馨提示信息
    $http.get('/register/apptPrompt').success(function(data) {
      $scope.prompt = data;
    }).error(function(data){
      toastService.show(data);
    });

    //返回上页
    $scope.goBack = function() {
      var index = 0;
      for (var i in $ionicHistory.viewHistory().views) {
        var view = $ionicHistory.viewHistory().views[i];
        if (view.stateName === 'registerDoctorTimeSelect') {
          index = view.index;
          break;
        }
      }
      $ionicHistory.goBack(index-$ionicHistory.currentView().index);
    };

    //挂号须知提示框
    var myPopup = null;
    $scope.showAgreement = function() {
      myPopup = $ionicPopup.show({
        template: '<div style="padding: 3px;font-size:15px">'+$scope.agreement+'</div>',
        title: '挂号须知',
        cssClass: 'agreement-popup',
        buttons: [
          {
            text: '我知道了',
            type: 'button-positive',
            onTap: function(e) {
              e.preventDefault();
              myPopup.close();
            }
          }
        ]
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
      $http.post('/register/registrations/registration', registration).success(function(data) {
        $state.go('paymentSelect', {orderNum: data.orderNum, memberId: $stateParams.memberId});
      }).error(function(data){
        toastService.show(data);
      });
    };

    $scope.$on('$ionicView.beforeLeave', function(){
      if (myPopup !== null) {
        myPopup.close();
      }
    });
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
