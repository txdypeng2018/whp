
iHospital App
=============

<a href="https://server.propersoft.cn/teamcity/viewType.html?buildTypeId=ISJ_FrontEnd">
  <img src="https://server.propersoft.cn/teamcity/app/rest/builds/buildType:(id:ISJ_FrontEnd)/statusIcon.svg"/>
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
# 生成源码格式的www目录，以便在设备上调试html和javascript
$ grunt srcDist

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

### 添加ngCordova相关插件方法

- 上[ngCordova](http://ngcordova.com/docs/plugins/)官网查找对应的插件
- 按照提示的方法安装插件
   例如安装toast插件,如果安装的url是一个github地址，则打开对应的地址，打开`package.json`查看这个插件的id。注意，要使用`ionic plugin add`，而不是官网上用的`cordova plugin add`
```bash
$ ionic plugin add cordova-plugin-x-toast
```
- 按ngCordova中文档的说明，使用相关插件

### android及ios程序调试方法

- [Chrome调试Android应用](http://ask.dcloud.net.cn/docs/#http://ask.dcloud.net.cn/article/69)
- [Safari调试iOS应用](http://ask.dcloud.net.cn/docs/#http://ask.dcloud.net.cn/article/143)
