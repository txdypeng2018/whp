'use strict';

(function () {

  var deps = [
    'ionic',
    'iosDblclick',
    'angular-carousel',
    'ngCordova',
    'properNgCordova'
  ];
  angular.module('isj', deps).run(

    function ($ionicPlatform, $window, $properProperpush, appConstants, $state, $ionicPopup, $http, $location, $ionicHistory, $rootScope, toastService) {
      $ionicPlatform.ready(function () {
        // Hide the accessory bar by default (remove this to show the accessory bar above the keyboard
        // for form inputs)
        if ($window.cordova && $window.cordova.plugins && $window.cordova.plugins.Keyboard) {
          $window.cordova.plugins.Keyboard.hideKeyboardAccessoryBar(false);
          $window.cordova.plugins.Keyboard.disableScroll(true);
        }
        if ($window.StatusBar) {
          // org.apache.cordova.statusbar required
          $window.StatusBar.styleDefault();
        }

        //push start
        //推送相关的参数
        var params = appConstants.properpush;
        //初始化推送
        $properProperpush.init(params).then(function () {
          console.log('推送初始化成功');
        }, function (error) {
          console.error('error:' + error);
        });
        //打开notification的回调接口
        function onOpenNotification(event) {
          //event.properAlert=='true',
          //说明是在ios设备上，当前应用程序正在打开状态，这时不会发送通知到状态栏
          //而是直接在程序里接收到通知，这时，可以在程序里显示一个alert,说明收到通知了
          if (event.properAlert) {
            //alert('ios 收到alert 通知'+JSON.stringify(event.properCustoms));
            if ('messages' === event.properCustoms.pageUrl) {
              var myPopup = $ionicPopup.show({
                template: '<div style="padding: 3px;font-size:15px; text-align:center;">' + '您有新的消息' + '</div>',
                title: '掌上盛京医院',
                buttons: [
                  {
                    text: '我知道了',
                    type: 'positive',
                    onTap: function (e) {
                      e.preventDefault();
                      myPopup.close();
                    }
                  },
                  {
                    text: '前往查看',
                    type: 'positive',
                    onTap: function (e) {
                      e.preventDefault();
                      myPopup.close();
                      $state.go('tab.message');
                    }
                  }
                ]
              });
            }
            //接收意见反馈推送消息
            else if ('feedback' === event.properCustoms.pageUrl) {
              var feedbackPopup = $ionicPopup.show({
                template: '<div style="padding: 3px;font-size:15px; text-align:center;">' + '您有新的意见反馈消息' + '</div>',
                title: '掌上盛京医院',
                buttons: [
                  {
                    text: '我知道了',
                    type: 'positive',
                    onTap: function (e) {
                      e.preventDefault();
                      myPopup.close();
                    }
                  },
                  {
                    text: '前往查看',
                    type: 'positive',
                    onTap: function (e) {
                      e.preventDefault();
                      feedbackPopup.close();
                      $state.go('settingFeedbackListNew');
                    }
                  }
                ]
              });
            }
          } else {
            //点击状态栏的通知，进入程序
            //alert('打开notification通知'+JSON.stringify(event.properCustoms));
            if ('messages' === event.properCustoms.pageUrl) {
              $state.go('tab.message');
            }
            //接收意见反馈推送消息
            else if ('feedback' === event.properCustoms.pageUrl) {
              $state.go('settingFeedbackListNew');
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

        $window.localStorage.serverContext = 'https://sjh.sj-hospital.org/isj';
        console.debug('Initial serverContext: ' + $window.localStorage.serverContext);
        var switchContext = function() {
          $http.get('http://172.28.235.106/isj/app/latest', {timeout: 500})
            .success(function() {
              $window.localStorage.serverContext = 'http://172.28.235.106/isj';
              console.debug('Swtich to internal context: ' + $window.localStorage.serverContext);
            })
            .error(function() {
              $window.localStorage.serverContext = 'https://sjh.sj-hospital.org/isj';
              console.debug('Swtich to public context: ' + $window.localStorage.serverContext);
            });
        };
        switchContext();
        document.addEventListener('resume', switchContext, false);

        //检查程序版本
        $properProperpush.getDeviceInfo().then(function (success) {
          cordova.getAppVersion.getVersionCode(function (versionCode) {
            $http.get('/app/latest', {
              params: {
                'current': versionCode,
                'device': success.type
              }
            }).success(function (data) {
              if (success.type === 'android') {
                window.plugins.UpdateVersion.isUpdating(function (s) {
                  //如果不是处在正在更新中，则检查程序版本
                  if (!s.updating) {
                    var versionInfo = {};
                    versionInfo.ver = data.ver || '0';
                    versionInfo.url = data.url || '';
                    versionInfo.note = data.note.replace(new RegExp(/(<br>)/g), '\n') || '有新版本需要更新！';
                    window.plugins.UpdateVersion.checkVersion(versionInfo);
                  }
                }, function (err) {
                  console.error(err);
                });
              }
            });
          });
        }, function (error) {
          console.error('error:' + error);
        });

      });
      //退出时提示
      var setBackBtn = function(){
        $rootScope.backButtonPressedOnceToExit = true;
        toastService.show('再按一次退出系统');
        setTimeout(function(){
          $rootScope.backButtonPressedOnceToExit = false;
        }, 2000);
      };
      $ionicPlatform.registerBackButtonAction(function(e){
        e.preventDefault();
        if($location.path() === '/tab/main'){
          if($rootScope.backButtonPressedOnceToExit){
            navigator.app.exitApp();
          }else{
            setBackBtn();
          }
        }else if($location.path() === '/tab/personal' || $location.path().indexOf('/tab/registration/') >= 0 || $location.path() === '/tab/message'){
          $state.go('tab.main');
        }else if($ionicHistory.backView()){
          $ionicHistory.goBack();
        }else{
          setBackBtn();
        }
        return false;
      },101);//101优先级常用于覆盖‘返回上一个页面’的默认行为
    }
  );

  if (typeof String.prototype.startsWith !== 'function') {
    String.prototype.startsWith = function (prefix) {
      return this.slice(0, prefix.length) === prefix;
    };
  }
  if (typeof String.prototype.endsWith !== 'function') {
    String.prototype.endsWith = function (suffix) {
      return this.indexOf(suffix, this.length - suffix.length) !== -1;
    };
  }
  if (typeof String.prototype.replaceAll !== 'function') {
    String.prototype.replaceAll = function (s1, s2) {
      return this.replace(new RegExp(s1, 'gm'), s2);
    };
  }
})();
