移动应用版本发布步骤
=================

### 构建安卓新版应用

1. 修改 `config.xml` 中 `<widget>` 元素的如下属性：

    - `version` 属性为即将发布的新版版本号，如 `3.1.0`。
    - `android-versionCode` 调整为发布版本的小版本号，即**新版本的起始构建序号**，如 `3.1.0` 版本的起始构建序号为 `3 01 00`。
    - `ios-CFBundleVersion` 同样调整为**新版本的起始构建序号**。

1. 修改持续集成环境 [General Settings](https://server.propersoft.cn/teamcities/admin/editBuild.html?id=buildType:Ihos_Build) 中的 `Build counter` 为**新版本的起始构建序号**。
1. 调整 [Build Features](https://server.propersoft.cn/teamcities/admin/editBuildFeatures.html?id=buildType:Ihos_Build) 中的 `File Content Replacer`，将 `Find what` 参数配置为**新版本的起始构建序号**。此后 `app-release` 分支的每次构建，都会自动更新安卓 apk 中的小版本号。**iOS 版发布时需额外手动调整**。
1. 打 tag

    ```
    # 先在本地打 tag
    $ git tag v3.1.0-sj-app
    # 将标签推送至远程仓库
    $ git push upstream v3.1.0-sj-app
    ```

1. 在仓库的 [releases](https://github.com/propersoft-cn/ihos/releases) 中，[Draft a new release](https://github.com/propersoft-cn/ihos/releases/new)，选择新推上来的发布标签，编写发版说明。

### iOS 发布新版应用

1. 拉取 `app-release` 分支最新代码，然后按照构建步骤生成 iOS 平台代码。
1. 修改文件 `config.xml` 中 `<widget>` 元素的如下属性：

    - `id` 需要由 `com.proper.soft.mobile.isj` 修改为 `com.neusoft.hcb.shengjing`。

    > 由于《掌上盛京医院》iOS 版应用最初的开发者是东软大象就医，继续使用该名称只能由原应用开发者转让应用所有权，故 `id` 不能变更。

    - `ios-CFBundleVersion` 需要调整为当前最新的 [构建序号](https://server.propersoft.cn/teamcities/viewType.html?buildTypeId=Ihos_Build&branch_Ihos=%3Cdefault%3E&tab=buildTypeStatusDiv)。

    > 因为构建版本上传后不能够删除，所以当上传或审核的版本出现问题时，可以在 `iTunes Contect` 中修改小版本号，以保证发布的大版本号不发生变化。

1. 发布时，在 Xcode 中打开 `PushNotification` 开关。
1. 填写发布信息时需要将发布支持的 iOS 系统选择 `iOS 9`。

### 更新热部署代码

[APP 热部署服务更新说明](./服务器106/opt/docker/isj_official/nginx/hc)

> **重要！勿忘记此步骤，否则新版上线后会被退回到之前版本！**

### 发布应用内更新通知

1. 通过 [应用宝](http://sj.qq.com) 找到已经发布在应用宝中的 [掌上盛京医院](http://sj.qq.com/myapp/detail.htm?apkName=com.proper.soft.mobile.isj)，并将下载地址记录下来。
1. 通过 `curl` 命令发版

```
$ curl -H "Content-Type:application/json" -H "Authorization:eyJpZCI6InBlcC1zeXNhZG1pbiIsIm5hbWUiOiJhZG1pbiJ9.eyJlbXBOYW1lIjpudWxsLCJyb2xlcyI6bnVsbH0.K_vRoOmRIONJUCvkI3ohDQSvYx4fESffbFiC5Lhtxbc" -X PUT -d '{"ver":301022, "note":" 版本号：3.1.0：1. 重新开放一网通支付方式，更多支付方式，更加方便快捷2. 挂号单中支付、确认及退款状态更详细3. 挂号单分类，更容易找到未就诊的挂号单4. 挂号时如有未支付的挂号单，可直接跳转至未支付页面进行支付5. 对性能进行优化，更节省流量", "url":"http://42.202.141.13/imtt.dd.qq.com/16891/4C2A85DABF7AEDD5408FE554CC3D5E26.apk?mkey=5852799b924608ce&f=b25&c=0&fsname=com.proper.soft.mobile.isj_3.1.0_301022.apk&csr=4d5s&p=.apk"}' https://sjh.sj-hospital.org/isj/app/latest
```

### 新版更新统计

找到 nginx 访问日志所在路径，从新版发布日期日志开始进行统计，如：

    $ cat req_stat_2017-03-08.log req_stat_2017-03-09.log | grep /latest.*current=30200.*Bearer | awk '{print $5}' | awk -F "." '{print $1}' | sort | uniq | wc -l
