#!/bin/bash
cd /var/lib/cbudgetbatch
/usr/bin/java -classpath /var/lib/cbudgetbatch/server.jar:/var/lib/cbudgetbatch/budget-Version3.jar:/var/lib/cbudgetbatch/postgresql-42.2.4.jar  cbudgetbatch/Server  budget budget $connectstring

