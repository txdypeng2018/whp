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
  .run(function($ionicPlatform,$window,$properProperpush,appConstants, $state, $ionicPopup,$http,$cordovaToast) {
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
  			//alert('ios 收到alert 通知'+JSON.stringify(event.properCustoms));
        if('messages' === event.properCustoms.pageUrl) {
          var myPopup = $ionicPopup.show({
            template: '<div style="padding: 3px;font-size:15px; text-align:center;">'+'您有新的消息'+'</div>',
            title: '掌上盛京医院',
            buttons: [
              {
                text: '我知道了',
                type: 'positive',
                onTap: function(e) {
                  e.preventDefault();
                  myPopup.close();
                }
              },
              {
                text: '前往查看',
                type: 'positive',
                onTap: function(e) {
                  e.preventDefault();
                  myPopup.close();
                  $state.go('tab.message');
                }
              }
            ]
          });
        }
  		}else{
  			//点击状态栏的通知，进入程序
  			//alert('打开notification通知'+JSON.stringify(event.properCustoms));
        if('messages' === event.properCustoms.pageUrl) {
          $state.go('tab.message');
        }
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

    //检查程序版本
  	function checkAppVersion(){
       $properProperpush.getDeviceInfo().then(function(success){
           if(success.type==='android'){
              $http.get('/app/latest', {params: {}}).success(function(data) {
                      var versionInfo={};
                      versionInfo.ver=data.ver||'0';
                      versionInfo.url=data.url||'';
                      versionInfo.note=data.note||'有新版本需要更新！';
                      window.plugins.UpdateVersion.checkVersion(versionInfo);
                    }).error(function(data){
                      $cordovaToast.showShortBottom(data);
               });

           }
        },function(error){alert('error:'+error);});
  	}
    document.addEventListener('resume', function() {
       //code for action on resume
       checkAppVersion();
    }, false);
    checkAppVersion();
    
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
