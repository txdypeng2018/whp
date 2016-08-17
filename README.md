Ionic SJ App
============

<a href="https://server.propersoft.cn/teamcity/viewType.html?buildTypeId=ISJ_Build">
  <img src="https://server.propersoft.cn/teamcity/app/rest/builds/buildType:(id:ISJ_Build)/statusIcon.svg"/>
</a>

```bash
$ npm install -g cordova ionic
$ npm install
$ bower install
# 编译静态资源并发布至 www 路径下
$ grunt

# 发布静态资源，通过浏览器访问，自动更新变化
$ grunt serve
# 或仅发布资源
$ ionic serve

# 一步恢复/重置平台和插件
$ ionic state restore/reset

# 也可根据情况选择添加 ios 或 android 平台
$ ionic platform add ios
$ ionic platform add android
# 恢复/重置所需插件
$ ionic state restore/reset --plugins

$ ionic build ios
$ ionic emulate ios

# 需要先安装SDK，并配置环境变量；JDK必须是1.8
$ ionic run android
# android 生成发布版本命令
$ ionic build android --release --  --buildConfig=build.json
```
