(function(app) {
  'use strict';

  var registerConfirmTodayCtrl = function($scope, $http, $state, $stateParams, $ionicPopup, $filter, $ionicHistory, $cordovaToast) {
    var doctorId = $stateParams.doctorId;
    var today = $stateParams.date;
    $scope.todayDisplay = today.substring(0,4)+'年'+today.substring(5,7)+'月'+today.substring(8,10)+'日'+today.substring(10,16);

    $scope.isChecked = true;

    //取得挂号须知
    $http.get('/register/agreement').success(function(data) {
      $scope.agreement = data;
      if (angular.isUndefined($stateParams.memberId) || $stateParams.memberId === '') {

      }
    }).error(function(data){
      $cordovaToast.showShortBottom(data);
    });
    //取得登录账号就医人
    var getPatient = function() {
      $http.get('/user/familyMembers/familyMember', {params: {memberId: $stateParams.memberId}}).success(function(data) {
        $scope.patient = data;
      }).error(function(data){
        $cordovaToast.showShortBottom(data);
      });
    };
    getPatient();
    //取得关系类别
    $http.get('/dataBase/familyMenberTypes').success(function(data) {
      $scope.memberTypes = data;
    }).error(function(data){
      $cordovaToast.showShortBottom(data);
    });
    //取得医生信息
    $http.get('/register/doctor', {params: {id: doctorId, date: today}}).success(function(data) {
      $scope.doctor = data;
    }).error(function(data){
      $cordovaToast.showShortBottom(data);
    });
    //取得温馨提示信息
    $http.get('/register/todayPrompt').success(function(data) {
      $scope.prompt = data;
    }).error(function(data){
      $cordovaToast.showShortBottom(data);
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
            text: '确定',
            type: 'positive',
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
      $http.post('/register/registrations/registration', registration).success(function(data) {
        $state.go('paymentSelect', {orderNum: data.orderNum});
      }).error(function(data){
        $cordovaToast.showShortBottom(data);
      });
    };

    $scope.$on('$ionicView.beforeLeave', function(){
      if (myPopup !== null) {
        myPopup.close();
      }
    });
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('registerConfirmToday', {
      url: '/register/registerConfirmToday/:doctorId/:date/:memberId',
      templateUrl: 'modules/register/registerConfirmToday.html',
      controller: registerConfirmTodayCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
