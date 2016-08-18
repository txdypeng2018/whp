'use strict';

(function() {

  var deps = [
    'ionic',
    'ionic-datepicker',
    'angular-carousel',
    'ngCordova'
    
  ];
  angular.module('isj', deps)
  .run(function($ionicPlatform,$window) {
	$ionicPlatform.ready(function() {
		// Hide the accessory bar by default (remove this to show the accessory bar above the keyboard
		// for form inputs)
		if($window.cordova && $window.cordova.plugins && $window.cordova.plugins.Keyboard) {
			$window.cordova.plugins.Keyboard.hideKeyboardAccessoryBar(true);
			$window.cordova.plugins.Keyboard.disableScroll(true);
		}
		if($window.StatusBar) {
			// org.apache.cordova.statusbar required
			$window.StatusBar.styleDefault();
		}
		
	});
});

  if (typeof String.prototype.startsWith !== 'function') {
    String.prototype.startsWith = function (prefix){
      return this.slice(0, prefix.length) === prefix;
    };
  }
  if (typeof String.prototype.endsWith !== 'function') {
    String.prototype.endsWith = function(suffix) {
      return this.indexOf(suffix, this.length - suffix.length) !== -1;
    };
  }
})();
