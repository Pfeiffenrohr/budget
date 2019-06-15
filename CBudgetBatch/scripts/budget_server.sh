#!/bin/bash
cd /var/lib/cbudgetbatch
/usr/bin/java -classpath /var/lib/cbudgetbatch/server.jar:/var/lib/cbudgetbatch/CbudgetBase-1.0.1-SNAPSHOT.jar:/var/lib/cbudgetbatch/postgresql-42.2.4.jar  cbudgetbatch/Server  budget budget $connectstring

