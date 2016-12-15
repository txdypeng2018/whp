'use strict';

(function(app) {
  // Config 'otherwise' redirection
  app.config(function($urlRouterProvider) {
    $urlRouterProvider.otherwise('/tab/main');
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
  app.factory('authInterceptor', function($q, $window, $rootScope, userService) {
    $rootScope.requestIndex = 0;
    var requestIndexMinus = function() {
      if ($rootScope.requestIndex >= 1) {
        $rootScope.requestIndex--;
      }
      if ($rootScope.requestIndex === 0) {
        $rootScope.inProcess = false;
      }
    };
    var excludeUrl = function(url) {
      return (url.indexOf('modules') < 0 && url.indexOf('/photo') < 0);
    };
    return {
      request: function(config) {
        if (excludeUrl(config.url)) {
          $rootScope.requestIndex++;
          $rootScope.inProcess = true;
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
        if (excludeUrl(rejection.config.url)) {
          requestIndexMinus();
        }
        console.debug('requestError %o', rejection);
        return $q.reject(rejection);
      },
      response: function(response) {
        if (excludeUrl(response.config.url)) {
          requestIndexMinus();
        }
        if (response.status === 401) {
          console.debug('handle the case where the user is not authenticated');
        }
        return response || $q.when(response);
      },
      responseError: function(rejection) {
        if (rejection.status === -1) {
          rejection.data = '网络异常，请稍后再试';
        }
        if (rejection.status === 401) {
          rejection.data = null;
        }
        if (rejection.status === 404) {
          rejection.data = '资源未找到';
        }
        if (rejection.status === 503) {
          rejection.data = '连接数过多，请稍后...';
        }
        if (rejection.status === 502) {
          rejection.data = '网络异常';
        }
        if(!angular.isUndefined(rejection.data) && rejection.data !== null && rejection.data.length > 25){
          rejection.data = '系统异常';
        }
        if (excludeUrl(rejection.config.url)) {
          requestIndexMinus();
        }
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
      piwikTracker.setRequestMethod('POST');
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
