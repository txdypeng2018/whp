(function(app) {
  'use strict';

  var tabMainCtrl = function($scope, $ionicHistory, $state, $http, $window, userService, toastService) {
    //取得软件名称
    $scope.appName = $window.localStorage.appName;
    $http.get('/service/appName').success(function(data) {
      $scope.appName = data;
      $window.localStorage.appName = data;
    }).error(function(){
      if (angular.isUndefined($scope.appName) || $scope.appName === '') {
        $scope.appName = '掌上盛京';
      }
    });

    //取得轮播图片
    var getWindowCarouselImages = function() {
      $scope.carouselImages = [];
      var index = 0;
      while(true) {
        index++;
        var imageName = $window.localStorage['carousel_'+index+'_name'];
        if (angular.isUndefined(imageName) || imageName === '') {
          break;
        }
        else {
          $scope.carouselImages.push({
            name: imageName,
            img: $window.localStorage['carousel_'+index+'_img']
          });
        }
      }
      if ($scope.carouselImages.length === 0) {
        return false;
      }
      else {
        return true;
      }
    };
    var clearWindowCarouselImages = function() {
      var index = 0;
      while(true) {
        index++;
        var imageName = $window.localStorage['carousel_'+index+'_name'];
        if (angular.isUndefined(imageName) || imageName === '') {
          break;
        }
        else {
          $window.localStorage.removeItem('carousel_'+index+'_name');
          $window.localStorage.removeItem('carousel_'+index+'_img');
        }
      }
    };
    var getHttpCarouselImages = function(newVersion) {
      $http.get('/service/carouselPhoto').success(function(data) {
        $scope.carouselImages = [];
        clearWindowCarouselImages();
        for (var i = 0 ; i < data.length ; i++) {
          $scope.carouselImages.push({
            name: data[i].name,
            img: data[i].img
          });
          var index = i + 1;
          $window.localStorage['carousel_'+index+'_name'] = data[i].name;
          $window.localStorage['carousel_'+index+'_img'] = data[i].img;
        }
        $window.localStorage.carouselVersion = newVersion;
      });
    };
    var flg = getWindowCarouselImages();
    var carouselVersion = $window.localStorage.carouselVersion;
    $http.get('/service/carouselPhoto/version').success(function(data) {
      if (angular.isUndefined(carouselVersion) || carouselVersion === '' || carouselVersion !== data || !flg) {
        getHttpCarouselImages(data);
      }
    }).error(function(){
      if (angular.isUndefined($scope.carouselImages) || $scope.carouselImages.length === 0) {
        $scope.carouselImages = [
          {
            name: '预约挂号',
            img: './assets/images/ad1.png'
          },
          {
            name: '在线缴费',
            img: './assets/images/ad2.png'
          },
          {
            name: '查看报告',
            img: './assets/images/ad3.png'
          }
        ];
      }
    });

    $scope.$on('$ionicView.beforeEnter', function(){
      $ionicHistory.clearHistory();
    });

    //路由跳转
    var itemRouterGo = function(routerId, type) {
      if (routerId === 'subjectSelect') {
        $state.go(routerId, {type: type});
      }
      else if (routerId === 'medicalReportList') {
        $state.go('medicalReportList', {title: '查看报告'});
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
              toastService.show(data);
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
