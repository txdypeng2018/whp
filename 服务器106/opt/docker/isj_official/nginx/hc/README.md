APP 热部署服务
============

1. 从 TeamCity 中找到要部署版本的构建成果物（Artifacts），点击进入 `Artifacts` 界面后，查看 URL 地址，从中得到 `buildId` 的参数
1. 在服务器相应路径中，执行 `$ ./update.sh $buildId` 即可完成新版的发布
1. 发布完成后，可通过 [https://sjh.sj-hospital.org/hc/chcp.json](https://sjh.sj-hospital.org/hc/chcp.json) 进行检验
