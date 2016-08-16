(function(app) {
  'use strict';

  var familyMemberAddCtrl = function($scope, $state, $timeout) {
    //家庭关系类别
    $scope.relationshipTypes = [
      {
        'name': '爸爸',
        'img': './assets/images/pic_old_man.png'
      },
      {
        'name': '妈妈',
        'img': './assets/images/pic_old_woman.png'
      },
      {
        'name': '配偶',
        'img': './assets/images/pic_woman.png'
      },
      {
        'name': '儿子',
        'img': './assets/images/pic_boy.png'
      },
      {
        'name': '女儿',
        'img': './assets/images/pic_girl.png'
      },
      {
        'name': '兄弟',
        'img': './assets/images/pic_man.png'
      },
      {
        'name': '配姐妹',
        'img': './assets/images/pic_woman.png'
      },
      {
        'name': '亲属',
        'img': './assets/images/pic_man.png'
      },
      {
        'name': '朋友',
        'img': './assets/images/pic_man.png'
      },
      {
        'name': '其他',
        'img': './assets/images/pic_man.png'
      }
    ];

    //家庭成员选择事件
    $scope.medicalCardEdit = function() {
      $timeout(function(){
        $state.go('familyMemberEdit');
      }, 10);
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('familyMemberAdd', {
      url: '/familyMember/familyMemberAdd',
      templateUrl: 'modules/familyMember/familyMemberAdd.html',
      controller: familyMemberAddCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
