APP 热部署服务
============

1. 从 TeamCity 中找到要部署版本的构建成果物（Artifacts），点击进入 `Artifacts` 界面后，查看 URL 地址，从中得到 `buildId` 的参数
1. 在服务器相应路径中，执行 `$ ./update.sh $buildId` 即可完成 **debug** 版的发布
> 注意：如果要进行新版的**正式发布**，需增加 `--release` 参数，如 `$ ./update.sh $buildId --release`
1. 发布完成后，可通过如下地址进行检验。**若未生效，需重启容器使生效**。
    * debug 版：[https://sjh.sj-hospital.org/hc_debug/chcp.json](https://sjh.sj-hospital.org/hc_debug/chcp.json)
    * 正式版： [https://sjh.sj-hospital.org/hc/chcp.json](https://sjh.sj-hospital.org/hc/chcp.json)
