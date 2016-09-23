(function(app) {
  'use strict';

  var tabMainCtrl = function($scope, $ionicHistory, $state, $http, userService, $cordovaToast) {
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
    var itemRouterGo = function(routerId, type) {
      if (routerId === 'subjectSelect') {
        $state.go(routerId, {type: type});
      }
      else if (routerId === 'medicalReportList') {
        $state.go('upgrading', {title: '查看报告'});
      }
      else {
        $state.go(routerId);
      }
    };
    $scope.itemRouter = function(routerId, type) {
      var isLogin = true;
      if (routerId === 'medicalReportList' || routerId === 'onlinePaymentList') {
        isLogin = userService.hasToken();
        if (isLogin) {
          $http.get('/user/tokenVal').success(function() {
            itemRouterGo(routerId, type);
          }).error(function(data, status){
            if (status !== 401) {
              $cordovaToast.showShortBottom(data);
            }
            else {
              userService.clearToken();
              $state.go('login');
            }
          });
        }
        else {
          $state.go('login');
        }
      }
      else {
        itemRouterGo(routerId, type);
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
