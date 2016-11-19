'use strict';

(function(app) {

  // Config app theme
  app.config(function($mdThemingProvider) {
    var genPalette = function(color) {
      return {
        '50': color,
        '100': color,
        '200': color,
        '300': color,
        '400': color,
        '500': color,
        '600': color,
        '700': color,
        '800': color,
        '900': color,
        'A100': color,
        'A200': color,
        'A400': color,
        'A700': color,
        'contrastDefaultColor': 'light'
      };
    };
    $mdThemingProvider.definePalette('peaBlue', genPalette('039BE5'));
    $mdThemingProvider.definePalette('peaGreen', genPalette('00C853'));
    $mdThemingProvider.definePalette('peaYellow', genPalette('FF9800'));

    $mdThemingProvider.alwaysWatchTheme(true);
    $mdThemingProvider.theme('skin-blue-light').primaryPalette('peaBlue').accentPalette('pink');
    $mdThemingProvider.theme('skin-blue-dark').primaryPalette('peaBlue').accentPalette('pink');
    $mdThemingProvider.theme('skin-green-light').primaryPalette('peaGreen').accentPalette('pink');
    $mdThemingProvider.theme('skin-green-dark').primaryPalette('peaGreen').accentPalette('pink');
    $mdThemingProvider.theme('skin-yellow-light').primaryPalette('peaYellow').accentPalette('blue');
    $mdThemingProvider.theme('skin-yellow-dark').primaryPalette('peaYellow').accentPalette('blue');
    $mdThemingProvider.setDefaultTheme('skin-blue-light');
  });

  // Config 'otherwise' redirection
  app.config(function($urlRouterProvider) {
    $urlRouterProvider.otherwise('/auth/login');
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
  var CONTEXT = './api';
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
      var piwikTracker = Piwik.getTracker( '//' + appConstants.piwikServer + '/piwik.php', 2 );
      //Set custom url
      piwikTracker.setCustomUrl(url);
      //Track the page view
      piwikTracker.trackPageView();
    });
  });

  app.config(function($httpProvider) {
    $httpProvider.interceptors.push('authInterceptor');
  });

})(angular.module('pea'));
