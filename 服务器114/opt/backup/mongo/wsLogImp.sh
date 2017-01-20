#!/bin/sh

export WS_DAY=`date +%Y%m%d`
export WS_BACKUP_DIR=/opt/backup/mongo/ws_log

/opt/mongodb-linux-x86_64-3.2.11/bin/mongoimport --host 172.28.235.114 --username admin --password 123456 --authenticationDatabase admin --db isj --collection WS_LOG --file $WS_BACKUP_DIR/$WS_DAY*.json
