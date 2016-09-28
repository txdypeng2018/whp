(function(app) {
  'use strict';

  var familyMemberEditCtrl = function($scope, $http, $stateParams, $ionicPopup, $cordovaToast, $ionicHistory) {
    //性别类别
    $http.get('/dataBase/sexTypes').success(function(data) {
      $scope.sexTypes = data;
      $scope.sexs = [];
      for (var i in $scope.sexTypes) {
        $scope.sexTypes[i].code = i;
        $scope.sexs.push($scope.sexTypes[i]);
      }
    }).error(function(data){
      $cordovaToast.showShortBottom(data);
    });

    //取得家庭成员信息
    var getFamilyMember = function() {
      $http.get('/user/familyMembers/familyMember',{params: {memberId: $stateParams.memberId}}).success(function(data) {
        var img = '';
        if (data.memberCode === '00' && data.sexCode === '1') {
          img = './assets/images/pic_man.png';
        }
        else if (data.memberCode === '00' && data.sexCode === '0') {
          img = './assets/images/pic_woman.png';
        }
        else {
          img = $scope.memberTypes[data.memberCode].img;
        }
        $scope.member = {
          memberCode: data.memberCode,
          member: data.member,
          sexCode: data.sexCode,
          sex: data.sex,
          name: data.name,
          img: img,
          idCard: data.idCard,
          phone: data.phone,
          patientVisits: (data.patientVisits === '1'),
          patientVisitsInit: (data.patientVisits === '1')
        };
      }).error(function(data){
        $cordovaToast.showShortBottom(data);
      });
    };

    $scope.$on('$ionicView.beforeEnter', function(){
      $scope.isSubmit = false;
      $scope.hasSex = false;
      $scope.member = {};

      if (angular.isUndefined($stateParams.memberId) || $stateParams.memberId === '') {
        $scope.isAdd = true;
        $http.get('/dataBase/familyMenberTypes/'+$stateParams.memberCode).success(function(data) {
          $scope.member = {
            memberCode: data.code,
            member: data.name,
            sexCode: data.sexCode,
            sex: '',
            name: '',
            img: data.img,
            idCard: '',
            phone: '',
            patientVisits: false
          };
          if (data.sexCode !== '') {
            $scope.member.sex = $scope.sexTypes[data.sexCode].name;
            $scope.hasSex = true;
          }
        }).error(function(data){
          $cordovaToast.showShortBottom(data);
        });
      }
      else {
        $scope.isAdd = false;
        $scope.hasSex = true;
        //家庭关系类别
        $http.get('/dataBase/familyMenberTypes').success(function(data) {
          $scope.memberTypes = data;
          getFamilyMember();
        }).error(function(data){
          $cordovaToast.showShortBottom(data);
        });
      }
    });

    //返回
    var goBack = function() {
      var index = 0;
      var skipId = 'familyMemberList';
      if (!angular.isUndefined($stateParams.skipId) && $stateParams.skipId !== '') {
        skipId = $stateParams.skipId;
      }
      for (var i in $ionicHistory.viewHistory().views) {
        var view = $ionicHistory.viewHistory().views[i];
        if (view.stateName === skipId) {
          index = view.index;
          break;
        }
      }
      $ionicHistory.goBack(index-$ionicHistory.currentView().index);
    };

    //验证身份证号
    var identityCodeValid = function(code) {
      var pass = false;
      if(code.length === 15 || code.length === 18){
        pass = true;
      }
      return pass;
    };

    //数据验证
    var dataValidation = function() {
      if ($scope.member.name === '') {
        $cordovaToast.showShortBottom('姓名不能为空');
        return false;
      }
      if ($scope.member.sexCode === '') {
        $cordovaToast.showShortBottom('性别不能为空');
        return false;
      }
      if ($scope.member.idCard === '') {
        $cordovaToast.showShortBottom('身份证号不能为空');
        return false;
      }
      if ($scope.member.phone === '') {
        $cordovaToast.showShortBottom('手机号不能为空');
        return false;
      }
      var reg = /^[0-9]+$/;
      if(!reg.test($scope.member.phone)){
        $cordovaToast.showShortBottom('手机号输入错误');
        return false;
      }
      if ($scope.member.phone.length !== 11) {
        $cordovaToast.showShortBottom('手机号输入错误');
        return false;
      }
      if(!identityCodeValid($scope.member.idCard)){
        $cordovaToast.showShortBottom('身份证号输入错误');
        return false;
      }
      return true;
    };

    //保存家庭成员
    $scope.saveMember = function() {
      $scope.isSubmit = true;
      if (dataValidation()) {
        if ($scope.isAdd) {
          var param1 = {
            name: $scope.member.name,
            sexCode: $scope.member.sexCode,
            sex: $scope.sexTypes[$scope.member.sexCode].name,
            idCard: $scope.member.idCard,
            phone: $scope.member.phone,
            memberCode: $scope.member.memberCode,
            member: $scope.member.member,
            patientVisits: $scope.member.patientVisits?'1':'0'
          };
          $http.post('/user/familyMembers/familyMember', param1).success(function() {
            goBack();
            $cordovaToast.showShortBottom('保存成功');
          }).error(function(data){
            $scope.isSubmit = false;
            $cordovaToast.showShortBottom(data);
          });
        } else {
          var param2 = {
            id: $stateParams.memberId,
            name: $scope.member.name,
            phone: $scope.member.phone,
            patientVisits: $scope.member.patientVisits?'1':'0'
          };
          $http.put('/user/familyMembers/familyMember', param2).success(function() {
            goBack();
            $cordovaToast.showShortBottom('保存成功');
          }).error(function(data){
            $scope.isSubmit = false;
            $cordovaToast.showShortBottom(data);
          });
        }
      }
      else {
        $scope.isSubmit = false;
      }
    };

    //删除家庭成员
    var myPopup = null;
    $scope.deleteMember = function() {
      myPopup = $ionicPopup.confirm({
        title: '提示',
        template: '<span style="font-size: 16px">是否删除该家庭成员?</span>',
        cancelText: '取消',
        okText: '确定'
      });
      myPopup.then(function(res) {
        if(res) {
          $scope.isSubmit = true;
          $http.delete('/user/familyMembers/familyMember', {params: {memberId: $stateParams.memberId}}).success(function() {
            goBack();
            $cordovaToast.showShortBottom('删除成功');
          }).error(function(data){
            $scope.isSubmit = false;
            $cordovaToast.showShortBottom(data);
          });
        }
      });
    };

    $scope.$on('$ionicView.beforeLeave', function(){
      if (myPopup !== null) {
        myPopup.close();
      }
    });
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('familyMemberEdit', {
      url: '/familyMember/familyMemberEdit/:memberId/:memberCode/:skipId',
      templateUrl: 'modules/familyMember/familyMemberEdit.html',
      controller: familyMemberEditCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
