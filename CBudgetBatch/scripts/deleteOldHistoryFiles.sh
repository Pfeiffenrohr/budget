#!/bin/bash
cd /var/lib/cbudgetbatch
while [ 1 -eq 1 ]
do
   /usr/bin/java  -classpath /var/lib/cbudgetbatch/server.jar:/var/lib/cbudgetbatch/budget-Version3.jar:/var/lib/cbudgetbatch/postgresql-42.2.4.jar  cbudgetbatch.cleanup.DeleteOldHistoryFiles budget budget $connectstring
   sleep 700600
done