#!/bin/bash
export WORKDIR=/opt/docker/isj_official/nginx/isjadm

if [ -d "$WORKDIR/repo" ];
then
    rm -rf $WORKDIR/repo_bak
    cp -r $WORKDIR/repo $WORKDIR/repo_bak
    cd $WORKDIR/repo
    echo "Pull deploy branch ..."
    git checkout .
    git pull https://github.com/propersoft-cn/ihos.git fe-deploy
fi

if [ ! -d "$WORKDIR/repo" ];
then
    echo "Cloning fe-deploy branch ..."
    git clone https://github.com/propersoft-cn/ihos.git -b fe-deploy --depth=1 $WORKDIR/repo
fi

sed -i 's/"\.\/api"/location.protocol+"\/\/"+location.host+"\/isj"/' $WORKDIR/repo/admin/www/scripts/scripts*.js
