#!/bin/bash
ps aux |grep budget_server|grep -v grep >/dev/null
if [ $? -ne 0 ]; then
host=`hostname`
echo "start server on $host ..." >> /home/budget/budget/log.txt
/home/budget/budget/budget_server.sh &
fi
