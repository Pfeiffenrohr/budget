#!/bin/bash
cd /home/budget/budget
/usr/bin/java -classpath /home/budget/budget/server.jar:/home/budget/budget/lib/CbudgetBase-1.0.1-SNAPSHOT.jar:/home/budget/budget/lib/postgresql-42.2.4.jar  cbudgetbatch/Server  budget budget budget >>/home/budget/budget/log.txt 2>&1
