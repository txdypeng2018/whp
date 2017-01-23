#!/bin/sh

export NGINX_DIR=/opt/docker/isj_official/nginx
export TODAY_TIME=`date +%Y-%m-%d`
export RENAME_TIME=`date -d "1 days ago" +%Y-%m-%d`
export REQ_DELTIME=`date -d "30 days ago" +%Y-%m-%d`

sed -i -e "s|req_stat_$RENAME_TIME.log|req_stat_$TODAY_TIME.log|" $NGINX_DIR/nginx.conf
/opt/docker/docker/docker restart isjofficial_isj-nginx_official_1

echo "Delete the nginx log..."
rm -rf $NGINX_DIR/log/req_stat_$REQ_DELTIME.log
echo "Delete the nginx log successfully. "

