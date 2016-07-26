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
  var CONTEXT = '/api';
  app.factory('authInterceptor', function($q, $window, $rootScope) {
    return {
      request: function(config) {
        $rootScope.inProcess = true;
        // Add JWT token in header
        config.headers = config.headers || {};
        if ($window.localStorage.token) {
          config.headers.Authorization = 'Bearer ' + $window.localStorage.token;
        }
        // Add context path of api server
        if (needContextPrefix(config.url)) {
          config.url = CONTEXT + config.url;
        }
        return config;
      },
      requestError: function(rejection) {
        $rootScope.inProcess = false;
        console.debug('requestError %o', rejection);
        return $q.reject(rejection);
      },
      response: function(response) {
        $rootScope.inProcess = false;
        if (response.status === 401) {
          console.debug('handle the case where the user is not authenticated');
        }
        return response || $q.when(response);
      },
      responseError: function(rejection) {
        $rootScope.inProcess = false;
        console.debug('responseError %o', rejection);
        return $q.reject(rejection);
      }
    };
  });
  app.config(function($httpProvider) {
    $httpProvider.interceptors.push('authInterceptor');
  });

})(angular.module('isj'));