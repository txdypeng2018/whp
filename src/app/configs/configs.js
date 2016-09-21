'use strict';

(function(app) {
  // Config 'otherwise' redirection
  app.config(function($urlRouterProvider) {
    $urlRouterProvider.otherwise('/tab/main');
  });

  app.config(function (ionicDatePickerProvider) {
    var datePickerObj = {
      setLabel: '选择',
      todayLabel: '今天',
      closeLabel: '关闭',
      weeksList: ['日', '一', '二', '三', '四', '五', '六'],
      monthsList: ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'],
      templateType: 'popup',
      showTodayButton: true,
      closeOnSelect: true,
      mondayFirst: true,
      dateFormat: 'yyyy年MM月dd日'
    };
    ionicDatePickerProvider.configDatePicker(datePickerObj);
  });

  /**
   * 判断请求 url 是否需要添加服务端上下文根前缀
   * @param url
   * @returns {boolean}
   */
  var needContextPrefix = function(url) {
    var isNeed = true;

    // Activiti modeler.html 不需要服务端上下文根前缀
    if (url.endsWith('.html')) {
      isNeed = false;
    }

    // md-data-table 组件图标 url(如:navigate-next.svg) 不需要服务端上下文根前缀
    if (url.endsWith('.svg') && !url.startsWith('/')) {
      isNeed = false;
    }

    return isNeed;
  };

  // Config JWT in http header and prefix of url
  var CONTEXT = 'https://sjh.sj-hospital.org/isj';
  var requestIndex = 0;
  app.factory('authInterceptor', function($q, $window, $rootScope, userService) {
    $rootScope.refreshProcess = 0;
    return {
      request: function(config) {
        if (config.url.indexOf('modules') < 0 && config.url.indexOf('/photo') < 0) {
          requestIndex++;
          $rootScope.inProcess = true;
          $rootScope.refreshProcess++;
        }
        // Add JWT token in header
        config.headers = config.headers || {};
        if (userService.hasToken()) {
          config.headers.Authorization = 'Bearer ' + userService.getToken();
        }
        // Add context path of api server
        if (needContextPrefix(config.url)) {
          config.url = CONTEXT + config.url;
        }
        return config;
      },
      requestError: function(rejection) {
        requestIndex = 0;
        $rootScope.inProcess = false;
        $rootScope.refreshProcess = 0;
        console.debug('requestError %o', rejection);
        return $q.reject(rejection);
      },
      response: function(response) {
        if (response.config.url.indexOf('modules') < 0 && response.config.url.indexOf('/photo') < 0) {
          requestIndex--;
          if (requestIndex <= 0) {
            $rootScope.inProcess = false;
            $rootScope.refreshProcess = 0;
          }
        }
        if (response.status === 401) {
          console.debug('handle the case where the user is not authenticated');
        }
        return response || $q.when(response);
      },
      responseError: function(rejection) {
        if (rejection.status === 404) {
          rejection.data = '资源未找到';
        }
        requestIndex = 0;
        $rootScope.inProcess = false;
        $rootScope.refreshProcess = 0;
        console.debug('responseError %o', rejection);
        return $q.reject(rejection);
      }
    };
  });

  //piwik 统计代码
  app.run(function($rootScope, $location, appConstants) {
    $rootScope.location = $location;
    $rootScope.$watch( 'location.url()', function( url ) {
      var _paq = _paq || [];
      //Prepend the site domain to the page title when tracking
      _paq.push(['setDocumentTitle', document.domain + '/' + document.title]);
      //In the 'Outlinks' report, hide clicks to known alias URLs of pea
      _paq.push(['setDomains', ['*.' + appConstants.piwikTrackTarget]]);
      _paq.push(['trackPageView']);
      _paq.push(['enableLinkTracking']);
      //Get piwik tracker
      var piwikTracker = Piwik.getTracker(appConstants.piwikServer + '/piwik.php', 3 );
      //Set custom url
      piwikTracker.setCustomUrl(url);
      //Track the page view
      piwikTracker.trackPageView();
    });
  });

  app.config(function($httpProvider) {
    $httpProvider.interceptors.push('authInterceptor');
  });

})(angular.module('isj'));
