(function(app) {
  'use strict';

  var mainCtrl = function($scope) {
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
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('tab.main', {
      url: '/main',
      views:{
        'tab-main':{
          templateUrl: 'modules/main/main.html',
          controller: mainCtrl
        }
      }
    });
  };

  app.config(mainRouter);
})(angular.module('isj'));
