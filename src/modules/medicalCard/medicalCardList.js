(function(app) {
  'use strict';

  var medicalCardListCtrl = function($scope, $http, $state) {
    //家庭关系类别
    $scope.relationshipTypes = {
      '00': {
        'name': '我',
        'img': './assets/images/pic_man.png'
      },
      '01': {
        'name': '爸爸',
        'img': './assets/images/pic_old_man.png'
      },
      '02': {
        'name': '妈妈',
        'img': './assets/images/pic_old_woman.png'
      },
      '03': {
        'name': '配偶',
        'img': './assets/images/pic_woman.png'
      }
    };

    //取得家庭成员列表
    $http.get('/familyMembers').success(function(data) {
      $scope.members = data;
      for (var i = 0 ; i < $scope.members.length ; i++) {
        $scope.members[i].idCard = $scope.members[i].idCard.substring(0,6)+'********'+$scope.members[i].idCard.substring(14,18);
      }
    });

    //家庭成员选择事件
    $scope.medicalCardEdit = function() {
      $state.go('medicalCardEdit');
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('medicalCardList', {
      url: '/medicalCard/medicalCardList',
      templateUrl: 'modules/medicalCard/medicalCardList.html',
      controller: medicalCardListCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
