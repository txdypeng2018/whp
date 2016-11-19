(function(app) {
  'use strict';

  var toggleSidenav = function($mdSidenav) {
    $mdSidenav('menu').toggle();
  };

  var quickSearchCtrl = function($scope, $http, $mdDialog) {
    $http.get('/auth/resources').success(function(data) {
      $scope.resources = data;
    });

    $scope.clickMenu = function(res) {
      $mdDialog.hide(res);
    };
  };

  var openQuickSearch = function($mdDialog, $scope, $location) {
    $mdDialog.show({
      controller: quickSearchCtrl,
      templateUrl: 'modules/main/quick-search.html',
      clickOutsideToClose: false
    }).then(function(res) {
      $scope.title = res.name;
      $location.url(res.url);
    });
  };

  var logout = function($document, $window, $state) {
    delete $window.localStorage.token;
    $state.go('login');
  };

  var mainCtrl = function($scope, $mdSidenav, $timeout, $location, $document, $mdDialog, $state, $window, hotkeys) {
    $scope.title = '';

    $scope.toggleSidenav = function() {
      toggleSidenav($mdSidenav);
    };

    $scope.openQuickSearch = function() {
      openQuickSearch($mdDialog, $scope, $location);
    };

    $scope.logout = function() {
      logout($document, $window, $state);
    };

    hotkeys.bindTo($scope)
      .add('a', '显示/隐藏账户侧栏', $scope.toggleSidenav)
      .add('q', '快速导航', $scope.openQuickSearch)
      .add('g g', '退出登录', $scope.logout);
  };

  var mainRouter = function($stateProvider) {
    $stateProvider.state('mainold', {
      url: '/mainold',
      templateUrl: 'modules/main/main.html',
      controller: mainCtrl
    });
  };

  app.config(mainRouter);
})(angular.module('pea'));
