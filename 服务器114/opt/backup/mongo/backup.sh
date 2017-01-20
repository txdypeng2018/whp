#!/bin/sh

export WS_DAY=`date -d "1 day ago" +%Y-%m-%d`
export WS_BACKUP_DIR=/opt/backup/mongo/ws_log
export BAKUPTIME=`date +%Y%m%d%H%M%S`

echo "Starting ws bakup..."
echo "Bakup file path $WS_BACKUP_DIR/$BAKUPTIME.json"
ws_start="$WS_DAY 00:00:00.000"
ws_end="$WS_DAY 23:59:59.000"
cmd="{'CT': {'\$gte': '$ws_start', '\$lt': '$ws_end'}, 'M': {'\$nin':  ['netTest','getHosInfo','getDeptInfoByParentID','getDeptInfo','getDoctorInfo','getRegInfo','getTimeRegInfo','getPayDetailAPP','getPayList','getPayDetail','getCheckOutReportList','getNormalReportInfo']}}"
/opt/mongodb-linux-x86_64-3.2.11/bin/mongoexport --host 172.28.235.115 --username admin --password 123456 --authenticationDatabase admin --db isj --collection WS_LOG --query "$cmd" --out $WS_BACKUP_DIR/$BAKUPTIME.json
echo "Bakup completed."
