'use strict';

(function() {

  var deps = [
    'ionic',
    'ionic-datepicker',
    'angular-carousel',
    'ngProgress',
    'ngCordova',
    'properNgCordova'
  ];
  angular.module('isj', deps)
  .run(function($ionicPlatform,$window,$properProperpush,appConstants) {
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

	//push start
	//推送相关的参数
	var params=appConstants.properpush;
  	//初始化推送
  	$properProperpush.init(params).then(function(){
  		console.log('推送初始化成功');
  	},function(error){alert('error:'+error);});
  	//打开notification的回调接口
  	function onOpenNotification(event){
  		//event.properAlert=='true',
  		//说明是在ios设备上，当前应用程序正在打开状态，这时不会发送通知到状态栏
  		//而是直接在程序里接收到通知，这时，可以在程序里显示一个alert,说明收到通知了
  		if(event.properAlert){
  			alert('ios 收到alert 通知'+JSON.stringify(event.properCustoms));
  		}else{
  			//点击状态栏的通知，进入程序
  			alert('打开notification通知'+JSON.stringify(event.properCustoms));
  		}
  		//event.properCustoms ，推送时自定义的键值对
  		//properCustoms 固定的系统键值对：
  		//_proper_userid 通知对应的userid
  		//_proper_title 通知的标题
  		//_proper_content 通知的正文

  	}
  	//添加打开通知的事件
  	document.addEventListener('Properpush.openNotification', onOpenNotification, false);
  	//push end

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
