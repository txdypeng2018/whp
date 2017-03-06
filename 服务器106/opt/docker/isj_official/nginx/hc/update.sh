#!/bin/bash
if [ ! -n "$1" ];
then
    echo "Need to pass build id"
    exit 1;
fi

export WORKDIR=/opt/docker/isj_official/nginx/hc
cd $WORKDIR
curl -O https://server.propersoft.cn/teamcities/guestAuth/repository/downloadAll/Ihos_Build/$1:id/artifacts.zip
rm -rf $WORKDIR/wwwbak
mv $WORKDIR/www $WORKDIR/wwwbak
unzip $WORKDIR/artifacts.zip -d $WORKDIR/www -x index.html *.apk