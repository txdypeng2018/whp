(function(app) {
  'use strict';

  var tabMainCtrl = function($scope, $ionicHistory, $state, userService) {
    $scope.$on('$ionicView.beforeEnter', function(){
      $ionicHistory.clearHistory();
    });

    //轮播图片
    $scope.carouselImages = [
      {
        name: '预约挂号',
        url: './assets/images/ad1.jpg'
      },
      {
        name: '在线缴费',
        url: './assets/images/ad2.png'
      },
      {
        name: '查看报告',
        url: './assets/images/ad3.png'
      }
    ];

    //路由跳转
    $scope.itemRouter = function(routerId, type) {
      var isLogin = true;
      if (routerId === 'medicalReportList' || routerId === 'onlinePaymentList') {
        isLogin = userService.hasToken();
      }
      if (isLogin) {
        if (routerId === 'subjectSelect') {
          $state.go(routerId, {type: type});
        }
        else {
          $state.go(routerId);
        }
      }
      else {
        $state.go('login');
      }
    };
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('tab.main', {
      url: '/main',
      views:{
        'tab-main':{
          templateUrl: 'modules/tab/main/main.html',
          controller: tabMainCtrl
        }
      }
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
