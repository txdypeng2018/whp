掌上盛京后台管理端
===============

更新代码
-------

```
$ cd /opt/docker/isj_official/nginx/isjadm
# 赋权限
$ chmod +x update.sh
# 更新，需 github 访问权限
$ ./update.sh
```

docker-compose 相关内容
----------------------

```
  - ./nginx/isjadm/repo/www:/etc/nginx/isjadm
```

nginx.conf 相关内容
------------------

```
location ^~ /isjadm/ {
  default_type application/octet-stream;
  root /etc/nginx;
}
```
