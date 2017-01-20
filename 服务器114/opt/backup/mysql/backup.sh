#!/bin/sh

export MYSQL_BACKUP_DIR=/opt/backup/mysql
export DELTIME=`date -d "3 days ago" +%Y%m%d`
export BAKUPTIME=`date +%Y%m%d%H%M%S`

echo "Starting mysql bakup..."
echo "Bakup file path $MYSQL_BACKUP_DIR/$BAKUPTIME.sql"
/opt/mysql-5.7.16-linux-glibc2.5-x86_64/bin/mysqldump -uroot -pProper123 -h172.28.235.114 -P33306 isj > $MYSQL_BACKUP_DIR/$BAKUPTIME.sql

echo "Delete the file bakup before 3 days..."
rm -rf $MYSQL_BACKUP_DIR/$DELTIME*.sql
echo "Delete the file bakup successfully. "

echo "Bakup completed."
