isjadm
======

```
# 构建镜像
$ docker build -t propersoft/docker-cd:isjadm .
# 启动容器
$ GH_OAUTH_TOKEN=xxxx docker-compose up -d
# admin 分支有变化，fe-deploy 部署完后，重启容器
$ docker restart isjadm_isjadm_1
```
