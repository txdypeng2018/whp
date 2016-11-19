'use strict';

(function(app) {

  //退出登录
  var logout = function($document, $window, $state) {
    delete $window.localStorage.token;
    $state.go('login');
  };

  var mainnewCtrl = function($scope, $rootScope, $document, $state, $window, $mdSidenav, $http, $location, hotkeys, screenSizeSm) {
    //左侧功能栏的li的高度(px)
    var liHeight = 43;
    var treeviewLiHeight = 32;

    //左侧功能栏默认显示方式（宽屏默认全显示，窄屏默认隐藏）
    $scope.isSidebarDisplayMini = false;
    $scope.isSidebarDisplayOpen = false;

    //即时查询框延时
    $scope.searchStrOptions = {
      debounce: 300
    };

    //皮肤参数
    $scope.skins = [
      { text: '蓝灰色', primary: 'blue',   light: true  },
      { text: '绿灰色', primary: 'green',  light: true  },
      { text: '黄灰色', primary: 'yellow', light: true  },
      { text: '蓝黑色', primary: 'blue',   light: false },
      { text: '绿黑色', primary: 'green',  light: false },
      { text: '黄黑色', primary: 'yellow', light: false }
    ];

    $scope.getSkinId = function(skin) {
      return skin.primary + '-' + (skin.light ? 'light' : 'dark');
    };

    //从浏览器本地存储中取得上次设置的皮肤
    $scope.mainSkin = $window.localStorage.mainSkin || 'skin-blue-light';
    $rootScope.peaMaterialTheme = $scope.mainSkin;

    //设置皮肤
    $scope.setSkin = function(skin) {
      $scope.mainSkin = 'skin-' + $scope.getSkinId(skin);
      $window.localStorage.mainSkin = $scope.mainSkin;
      $rootScope.peaMaterialTheme = $scope.mainSkin;
    };

    //全功能导航页隐藏
    $scope.resourceNavShow = false;

    //功能树查询字段初期化
    $scope.menuSearchStr = '';

    //全功能导航页面参数初期化
    $scope.resnav = {
      resourceall: [],
      searchStr: '',
      cardShow: {},
      moduleSelected: {},
      typeContentHeight: {}
    };

    //取得用户的功能树
    $http.get('/main/resources').success(function(data) {
      $scope.resources = data;
    });

    //从浏览器本地存储取得宽屏下上次左侧功能栏设置的显示方式
    var isSidebarDisplayMini = $window.localStorage.isSidebarDisplayMini;
    if (isSidebarDisplayMini !== null || !angular.isUndefined(isSidebarDisplayMini)) {
      if (isSidebarDisplayMini === 'true') {
        $scope.isSidebarDisplayMini = true;
      }
    }

    //取得ul下li的个数
    var getLiNumber = function(node) {
      var liNumber = 0;
      for (var i = 0 ; i < node.childNodes.length ; i++) {
        var childNode = node.childNodes[i];
        if(childNode.nodeName === 'LI') {
          liNumber++;
        }
      }
      return liNumber;
    };

    //左侧功能栏全收起
    var treeViewCloseAll = function(liNodes) {
      for (var i = 0 ; i <  liNodes.length ; i++) {
        var liNode = liNodes[i];
        if (angular.element(liNode).hasClass('active')) {
          angular.element(liNode).removeClass('active');
          for (var j = 0 ; j < liNode.childNodes.length ; j++) {
            var ulNode = liNode.childNodes[j];
            if (ulNode.nodeName === 'UL') {
              ulHidden(ulNode, treeviewLiHeight);
            }
          }
        }
      }
    };

    //左侧功能栏菜单隐藏
    var ulHidden = function(node) {
      node.style.height = node.offsetHeight + 'px';
      setTimeout(function(){node.style.height = '0px';}, 10);
    };

    //左侧功能树打开关闭
    var sidebarMenuOpenClose = function($event) {
      //点击发生的li元素
      var clickNode = $event.currentTarget.parentNode;

      //点击li元素下的treeview-menu
      var treeViewNode = $event.currentTarget.parentNode.getElementsByTagName('ul')[0];

      if (angular.element(clickNode).hasClass('active')) {
        angular.element(clickNode).removeClass('active');
        ulHidden(treeViewNode, treeviewLiHeight);
      }
      else {
        treeViewCloseAll(clickNode.parentNode.getElementsByTagName('li'));

        if ($window.innerWidth < screenSizeSm ||
            ($window.innerWidth >= screenSizeSm && !$scope.isSidebarDisplayMini) ||
            ($window.innerWidth >= screenSizeSm && $scope.isSidebarDisplayMini && !angular.element(clickNode).hasClass('treeview'))) {
          angular.element(clickNode).addClass('active');
          treeViewNode.style.height = (getLiNumber(treeViewNode) * treeviewLiHeight) + 'px';
          setTimeout(
            function(){
              if (angular.element(clickNode).hasClass('active')) {
                treeViewNode.style.height = 'auto';
              }
            },300);
        }
      }
    };

    //主页内容区路由
    var mainContentRouter = function(url) {
      $location.url(url);
    };

    //功能树递归查询
    var searchRecursive = function(treeMenu, searchStr) {
      var isHaveAll = false;
      for (var i = 0 ; i < treeMenu.length ; i++) {
        var resource = treeMenu[i];
        var isHave = false;
        if (resource.isLeaf === '0') {
          isHave = searchRecursive(resource.treeMenu, searchStr);
        }
        if (!isHave && resource.name.indexOf(searchStr) < 0) {
          resource.isSearch = '0';
        }
        else {
          isHaveAll = true;
          delete resource.isSearch;
        }
      }
      return isHaveAll;
    };

    //清空功能树查询结果
    var searchClear = function(treeMenu) {
      for (var i = 0 ; i < treeMenu.length ; i++) {
        var resource = treeMenu[i];
        if (resource.isLeaf === '0') {
          searchClear(resource.treeMenu);
        }
        delete resource.isSearch;
      }
    };

    //功能树增加
    var newResModule = function(resModule) {
      var resModuleNew = {};
      resModuleNew.code = resModule.code;
      resModuleNew.name = resModule.name;
      resModuleNew.level = 2;
      resModuleNew.isLeaf = '1';
      resModuleNew.icon = resModule.icon;
      resModuleNew.url = resModule.url;
      resModuleNew.orderNum = resModule.orderNum;
      return resModuleNew;
    };
    var sidebarMenuAdd = function(resModules, resType) {
      var flg = false;
      var resTypeNew = {};
      var resModuleNew = {};
      for (var i = 0 ; i < $scope.resources.length ; i++) {
        var resource = $scope.resources[i];
        if (resType.code === resource.code) {
          flg = true;
          for (var k = 0 ; k < resModules.length ; k++) {
            var moduleFlg = false;
            for (var m = 0 ; m < resource.treeMenu.length ; m++) {
              if (resModules[k].code === resource.treeMenu[m].code) {
                moduleFlg = true;
                break;
              }
            }
            if (!moduleFlg) {
              resModuleNew = newResModule(resModules[k]);
              resource.treeMenu.push(resModuleNew);
            }
          }
          resource.treeMenu.sort(function(a, b){return a.orderNum - b.orderNum;});
          break;
        }
      }

      if (!flg) {
        resTypeNew = {};
        resTypeNew.code = resType.code;
        resTypeNew.name = resType.name;
        resTypeNew.level = 1;
        resTypeNew.isLeaf = '0';
        resTypeNew.icon = resType.icon;
        resTypeNew.treeMenu = [];
        for (var j = 0 ; j < resModules.length ; j++) {
          resModuleNew = newResModule(resModules[j]);
          resTypeNew.treeMenu.push(resModuleNew);
        }
        $scope.resources.push(resTypeNew);
      }
    };

    //功能树删除
    var sidebarMenuRemove = function(resModules, resType) {
      for (var i = ($scope.resources.length - 1) ; i >= 0 ; i--) {
        var resource = $scope.resources[i];
        if (resource.code === resType.code && resource.treeMenu !== null && !angular.isUndefined(resource.treeMenu)) {
          for (var j = (resource.treeMenu.length - 1) ; j >= 0 ; j--) {
            var flg = false;
            for (var k = 0 ; k < resModules.length ; k++) {
              if (resource.treeMenu[j].code === resModules[k].code) {
                flg = true;
                break;
              }
            }
            if (flg) {
              resource.treeMenu.splice(j, 1);
            }
          }
          if (resource.treeMenu.length === 0) {
            $scope.resources.splice(i, 1);
          }
          break;
        }
      }
    };

    //左侧功能栏缩小后滚动条隐藏情况下的滚轮翻页
    document.getElementById('menu_sidebar').addEventListener('wheel', function(event) {
      if ($window.innerWidth >= screenSizeSm && $scope.isSidebarDisplayMini) {
        var menuSidebar = document.getElementById('sidebar_position');
        if (event.deltaY > 0) {
          //滚轮向下滚动
          if (document.getElementById('menu_ul').offsetHeight + menuSidebar.offsetTop - liHeight >= 50) {
            menuSidebar.style.marginTop = menuSidebar.offsetTop - 50 - liHeight + 'px';
          }
        }
        else {
          //滚轮向上滚动
          if (menuSidebar.offsetTop < 50) {
            menuSidebar.style.marginTop = menuSidebar.offsetTop - 50 + liHeight + 'px';
          }
        }
      }
    });

    //左侧功能树点击事件
    $scope.sidebarMenuOpenClk = function(resource, $event) {
      if (resource.isLeaf === '1') {
        mainContentRouter(resource.url);
      }
      else {
        sidebarMenuOpenClose($event);
      }
    };

    //左侧功能栏查询事件
    $scope.sidebarMenuSearch = function() {
      var resource = {};
      if ($scope.menuSearchStr === '' || $scope.menuSearchStr === null) {
        treeViewCloseAll(document.getElementById('menu_ul').getElementsByTagName('li'));

        for (var j = 0 ; j < $scope.resources.length ; j++) {
          resource = $scope.resources[j];
          if (resource.isLeaf === '0') {
            searchClear(resource.treeMenu);
          }
          delete resource.isSearch;
        }
      }
      else {
        for (var i = 0 ; i < $scope.resources.length ; i++) {
          resource = $scope.resources[i];
          var isHave = false;
          if (resource.isLeaf === '0') {
            isHave = searchRecursive(resource.treeMenu, $scope.menuSearchStr);
          }
          if (!isHave && resource.name.indexOf($scope.menuSearchStr) < 0) {
            resource.isSearch = '0';
          }
          else {
            delete resource.isSearch;
          }
        }

        setTimeout(function() {
          for (var k = 0 ; k < document.getElementById('menu_ul').getElementsByTagName('li').length ; k++) {
            var node = document.getElementById('menu_ul').getElementsByTagName('li')[k];
            if (node.getElementsByTagName('ul').length > 0) {
              if ($window.innerWidth < screenSizeSm ||
                ($window.innerWidth >= screenSizeSm && !$scope.isSidebarDisplayMini) ||
                ($window.innerWidth >= screenSizeSm && $scope.isSidebarDisplayMini && !angular.element(node).hasClass('treeview'))) {
                angular.element(node).addClass('active');
                node.getElementsByTagName('ul')[0].style.height = 'auto';
              }
            }
          }
        }, 10);
      }
    };
    $scope.$watch('menuSearchStr', function (newValue, oldValue) {
      if (newValue !== oldValue) {
        $scope.sidebarMenuSearch();
      }
    });

    //左侧功能栏宽屏下切换样式按钮事件
    $scope.sidebarToggleClk = function() {
      if ($window.innerWidth < screenSizeSm) {
        $scope.isSidebarDisplayOpen = !$scope.isSidebarDisplayOpen;
      }
      else {
        $scope.isSidebarDisplayMini = !$scope.isSidebarDisplayMini;

        $window.localStorage.isSidebarDisplayMini = $scope.isSidebarDisplayMini;

        if ($scope.isSidebarDisplayMini) {
          treeViewCloseAll(document.getElementById('menu_ul').getElementsByTagName('li'));
        }
        else {
          document.getElementById('sidebar_position').removeAttribute('style');
        }

        //模拟触发resize事件
        var resizeEvent = document.createEvent('HTMLEvents');
        resizeEvent.initEvent('resize', false, true);
        $window.dispatchEvent(resizeEvent);
      }
    };

    //通知右侧边栏打开关闭
    $scope.toggleRightNotifications = function() {
      if ($mdSidenav('rightSettings').isOpen()) {
        $mdSidenav('rightSettings').close();
        setTimeout(function(){$mdSidenav('rightNotifications').toggle();},300);
      }
      else {
        $mdSidenav('rightNotifications').toggle();
      }
    };

    //设置右侧边栏打开关闭
    $scope.toggleRightSettings = function() {
      if ($mdSidenav('rightNotifications').isOpen()) {
        $mdSidenav('rightNotifications').close();
        setTimeout(function(){$mdSidenav('rightSettings').toggle();},300);
      }
      else {
        $mdSidenav('rightSettings').toggle();
      }
    };

    //窄屏下点击内容区隐藏菜单
    $scope.sidebarHiddenClk = function() {
      if ($window.innerWidth < screenSizeSm && $scope.isSidebarDisplayOpen) {
        $scope.isSidebarDisplayOpen = false;
      }
    };

    //全功能导航页面打开关闭
    $scope.toggleResourceNav = function() {
      $scope.resourceNavShow = !$scope.resourceNavShow;
      if ($scope.resourceNavShow) {
        $http.get('/main/resourceall').success(function(data) {
          $scope.resnav.resourceall = data;
          for (var i = 0 ; i < $scope.resnav.resourceall.length ; i++) {
            var resource = $scope.resnav.resourceall[i];
            $scope.resnav.cardShow['navTypeContentShow_' + resource.code] = true;
            //选中参数初期化
            for (var j = 0 ; j < resource.treeMenu.length ; j++) {
              $scope.resnav.moduleSelected['selected_' + resource.treeMenu[j].code] = [];
            }
          }
          //选中功能树的模块
          for (var k = 0 ; k < $scope.resources.length ; k++) {
            if ($scope.resnav.moduleSelected['selected_' + $scope.resources[k].code] !== null &&
                !angular.isUndefined($scope.resnav.moduleSelected['selected_' + $scope.resources[k].code]) &&
                $scope.resources[k].treeMenu !== null && !angular.isUndefined($scope.resources[k].treeMenu)) {
              for (var x = 0 ; x < $scope.resources[k].treeMenu.length ; x++) {
                $scope.resnav.moduleSelected['selected_' + $scope.resources[k].code].push($scope.resources[k].treeMenu[x].code);
              }
            }
          }
        });
      }
      else {
        $scope.resnav = {
          resourceall: [],
          searchStr: '',
          cardShow: {},
          moduleSelected: {},
          typeContentHeight: {}
        };
      }
    };

    //全功能导航页面锚点
    $scope.resourceNavScroll = function(code) {
      document.getElementById('resnavCard_' + code).scrollIntoView(true);
    };

    //全功能导航页面路由
    $scope.resourceNavRouter = function(url) {
      $scope.resourceNavShow = false;
      mainContentRouter(url);
    };

    //全功能导航页面分类隐藏显示状态
    $scope.navTypeContentShow = function(code) {
      return $scope.resnav.cardShow['navTypeContentShow_'+code];
    };

    //全功能导航页面分类隐藏显示事件
    $scope.toggleNavTypeContent = function(code) {
      //隐藏显示动画
      var contentHeight = 0;
      if ($scope.resnav.cardShow['navTypeContentShow_'+code]) {
        contentHeight = document.getElementById('navTypeContent_'+code).offsetHeight;
        $scope.resnav.typeContentHeight['height_'+code] = contentHeight;
        document.getElementById('navTypeContent_'+code).style.height = contentHeight + 'px';
        setTimeout(function(){document.getElementById('navTypeContent_'+code).style.height = '0px';}, 10);
      }
      else {
        contentHeight = $scope.resnav.typeContentHeight['height_'+code];
        document.getElementById('navTypeContent_'+code).style.height = contentHeight + 'px';
        setTimeout(function(){document.getElementById('navTypeContent_'+code).style.height = 'auto';}, 300);
      }
      //改变隐藏显示参数
      $scope.resnav.cardShow['navTypeContentShow_'+code] = !$scope.resnav.cardShow['navTypeContentShow_'+code];
    };

    //全功能导航页面查询
    $scope.resnavSearch = function () {
      var resource = {};
      if ($scope.resnav.searchStr === '' || $scope.resnav.searchStr === null) {
        for (var k = 0 ; k < $scope.resnav.resourceall.length ; k++) {
          $scope.resnav.cardShow['navTypeContentShow_' + $scope.resnav.resourceall[k].code] = true;
          if (document.getElementById('navTypeContent_'+$scope.resnav.resourceall[k].code) !== null &&
              !angular.isUndefined(document.getElementById('navTypeContent_'+$scope.resnav.resourceall[k].code))) {
            document.getElementById('navTypeContent_'+$scope.resnav.resourceall[k].code).style.height = 'auto';
          }
        }

        for (var j = 0 ; j < $scope.resnav.resourceall.length ; j++) {
          resource = $scope.resnav.resourceall[j];
          if (resource.isLeaf === '0') {
            searchClear(resource.treeMenu);
          }
          delete resource.isSearch;
        }
      }
      else {
        for (var x = 0 ; x < $scope.resnav.resourceall.length ; x++) {
          $scope.resnav.cardShow['navTypeContentShow_' + $scope.resnav.resourceall[x].code] = true;
          if (document.getElementById('navTypeContent_'+$scope.resnav.resourceall[x].code) !== null &&
            !angular.isUndefined(document.getElementById('navTypeContent_'+$scope.resnav.resourceall[x].code))) {
            document.getElementById('navTypeContent_'+$scope.resnav.resourceall[x].code).style.height = 'auto';
          }
        }

        for (var i = 0 ; i < $scope.resnav.resourceall.length ; i++) {
          resource = $scope.resnav.resourceall[i];
          var isHave = false;
          if (resource.isLeaf === '0') {
            isHave = searchRecursive(resource.treeMenu, $scope.resnav.searchStr);
          }
          if (!isHave && resource.name.indexOf($scope.resnav.searchStr) < 0) {
            resource.isSearch = '0';
          }
          else {
            delete resource.isSearch;
          }
        }
      }
    };
    $scope.$watch('resnav.searchStr', function (newValue, oldValue) {
      if (newValue !== oldValue) {
        $scope.resnavSearch();
      }
    });

    //全功能导航页面checkbox联动
    $scope.toggleResModule = function (resModule, resType) {
      var idx = $scope.resnav.moduleSelected['selected_' + resType.code].indexOf(resModule.code);
      if (idx > -1) {
        $scope.resnav.moduleSelected['selected_' + resType.code].splice(idx, 1);
        sidebarMenuRemove([resModule], resType);
      }
      else {
        $scope.resnav.moduleSelected['selected_' + resType.code].push(resModule.code);
        sidebarMenuAdd([resModule], resType);
      }
    };
    $scope.existsResModule = function (resModule, resType) {
      return $scope.resnav.moduleSelected['selected_' + resType.code].indexOf(resModule.code) > -1;
    };
    var getModuleSelectedLength = function(resource, resLength) {
      if (!angular.isUndefined(resource.treeMenu) && resource.treeMenu !== null) {
        if (resource.treeMenu[0].isLeaf === '1') {
          resLength[0] += resource.treeMenu.length;
          resLength[1] += $scope.resnav.moduleSelected['selected_' + resource.code].length;
        }
        else {
          for (var i = 0 ; i < resource.treeMenu.length ; i++) {
            resLength = getModuleSelectedLength(resource.treeMenu[i], resLength);
          }
        }
      }
      return resLength;
    };
    $scope.isIndeterminateResModule = function(resource) {
      var resLength = getModuleSelectedLength(resource, [0,0]);
      return resLength[1] !== 0 && resLength[1] !== resLength[0];
    };
    $scope.isCheckedResModule = function(resource) {
      var resLength = getModuleSelectedLength(resource, [0,0]);
      return resLength[1] === resLength[0];
    };
    $scope.toggleResModuleAll = function(resource) {
      var resLength = getModuleSelectedLength(resource, [0,0]);
      if (resLength[1] === resLength[0]) {
        if (resource.treeMenu[0].isLeaf === '1') {
          $scope.resnav.moduleSelected['selected_' + resource.code] = [];
          sidebarMenuRemove(resource.treeMenu, resource);
        }
        else {
          for (var i = 0 ; i < resource.treeMenu.length ; i++) {
            $scope.resnav.moduleSelected['selected_' + resource.treeMenu[i].code] = [];
            sidebarMenuRemove(resource.treeMenu[i].treeMenu, resource.treeMenu[i]);
          }
        }
      }
      else if (resLength[1] >= 0) {
        if (resource.treeMenu[0].isLeaf === '1') {
          $scope.resnav.moduleSelected['selected_' + resource.code] = [];
          for (var j = 0 ; j < resource.treeMenu.length ; j++) {
            if ($scope.resnav.moduleSelected['selected_' + resource.code].indexOf(resource.treeMenu[j].code) < 0) {
              $scope.resnav.moduleSelected['selected_' + resource.code].push(resource.treeMenu[j].code);
            }
          }
          sidebarMenuAdd(resource.treeMenu, resource);
        }
        else {
          for (var m = 0 ; m < resource.treeMenu.length ; m++) {
            $scope.resnav.moduleSelected['selected_' + resource.treeMenu[m].code] = [];
            for (var n = 0 ; n < resource.treeMenu[m].treeMenu.length ; n++) {
              if ($scope.resnav.moduleSelected['selected_' + resource.treeMenu[m].code].indexOf(resource.treeMenu[m].treeMenu[n].code) < 0) {
                $scope.resnav.moduleSelected['selected_' + resource.treeMenu[m].code].push(resource.treeMenu[m].treeMenu[n].code);
              }
            }
            sidebarMenuAdd(resource.treeMenu[m].treeMenu, resource.treeMenu[m]);
          }
        }
      }
    };

    //设置内容区高度
    $scope.$watch('$viewContentLoaded', function() {
      document.getElementById('content_wrapper').style.minHeight =
        (document.body.clientHeight - 39) + 'px';
    });
    angular.element($window).bind('resize', function() {
      document.getElementById('content_wrapper').style.minHeight =
        (document.body.clientHeight - 39) + 'px';
    });

    //退出登录
    $scope.logout = function() {
      logout($document, $window, $state);
    };

    //热键
    hotkeys.bindTo($scope)
      .add('h', '切换功能栏样式', $scope.sidebarToggleClk)
      .add('j', '全功能导航页面', $scope.toggleResourceNav)
      .add('n', '通知页面', $scope.toggleRightNotifications)
      .add('m', '设置页面', $scope.toggleRightSettings)
      .add('esc', '关闭功能', function(){
        if ($scope.resourceNavShow) {
          $scope.resourceNavShow = false;
        }
        if ($window.innerWidth < screenSizeSm && $scope.isSidebarDisplayOpen) {
          $scope.isSidebarDisplayOpen = false;
        }
      })
      .add('g g', '退出登录', $scope.logout);
  };

  app.constant('screenSizeSm','768');
  var mainRouter = function($stateProvider) {
    $stateProvider.state('main', {
      url: '/',
      templateUrl: 'modules/mainnew/main.html',
      controller: mainnewCtrl
    });
  };
  app.config(mainRouter);
})(angular.module('pea'));

//改写material的mdMenu指令，增加$mdOpenCloseMenu打开关闭事件
angular.module('material.components.menu').directive('mdMenu', function() {
  return {
    require: '^mdMenu',
    link: function($scope, $element, $attr, mdMenuCtrl) {
      $scope.$mdOpenCloseMenu = function($event) {
        if (!$scope.$mdMenuIsOpen || angular.isUndefined($scope.$mdMenuIsOpen)) {
          mdMenuCtrl.open($event);
        }
        else {
          mdMenuCtrl.close($event);
        }
      };
    }
  };
});

//防止ng-click时关闭mdMenu
angular.module('pea').directive('myClick', function ($parse) {
  return {
    restrict: 'A',
    compile: function ($element, attrs) {
      var fn = $parse(attrs.myClick, null, true);
      return function myClick(scope, element) {
        element.on('click', function (event) {
          var callback = function () {
            fn(scope, { $event: event });
          };
          scope.$apply(callback);
        });
      };
    }
  };
});
