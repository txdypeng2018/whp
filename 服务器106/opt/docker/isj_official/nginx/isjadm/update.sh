#!/bin/bash
export WORKDIR=/opt/docker/isj_official/nginx/isjadm/repo

if [ -d "$WORKDIR" ];
then
    cd $WORKDIR
    echo "Pull deploy branch ..."
    git checkout .
    git pull https://github.com/propersoft-cn/ihos.git fe-deploy
fi

if [ ! -d "$WORKDIR" ];
then
    echo "Cloning fe-deploy branch ..."
    git clone https://github.com/propersoft-cn/ihos.git -b fe-deploy --depth=1 $WORKDIR
fi

sed -i 's/"\.\/api"/location.protocol+"\/\/"+location.host+"\/isj"/' $WORKDIR/www/scripts/scripts*.js
