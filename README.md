Ionic SJ App
============

<a href="https://server.propersoft.cn/teamcity/viewType.html?buildTypeId=ISJ_Build">
  <img src="https://server.propersoft.cn/teamcity/app/rest/builds/buildType:(id:ISJ_Build)/statusIcon.svg"/>
</a>

```bash
$ npm install -g cordova ionic
$ npm install
$ bower install

# 需要先执行grunt命令生成www文件夹
$ ionic platform add ios
$ ionic platform add android

# 需要先安装SDK，并配置环境变量；JDK必须是1.8
$ ionic build ios
$ ionic emulate ios

$ ionic run android

$ ionic serve

$ grunt serve
```
