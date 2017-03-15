#!/bin/bash
if [ ! -n "$1" ];
then
    echo "Need to pass build id"
    exit 1;
fi

WORKDIR=/opt/docker/isj_official/nginx/hc
HC_HOME=/www_debug
HC_BAK_HOME=/www_debug
TC_HOME=Ihos_APPDebug

if [ "$2" == "--release" ];
then
    HC_HOME=/www
    HC_BAK_HOME=/www_bak
    TC_HOME=Ihos_Build
fi

cd $WORKDIR
URL=https://server.propersoft.cn/teamcities/guestAuth/repository/downloadAll/$TC_HOME/$1:id/artifacts.zip
echo 'Downloading from '$URL
curl -O $URL
if [ -e $WORKDIR/artifacts.zip ];
then
    rm -rf $WORKDIR$HC_BAK_HOME
    mv $WORKDIR$HC_HOME $WORKDIR$HC_BAK_HOME
    echo 'Unziping artifacts.zip...'
    unzip -qq $WORKDIR/artifacts.zip -d $WORKDIR$HC_HOME -x index.html *.apk
    rm -f $WORKDIR/artifacts.zip
    echo 'Done'
fi