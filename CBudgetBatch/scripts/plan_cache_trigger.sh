#!/bin/bash
cd /var/lib/cbudgetbatch
while [ 1 -eq 1 ]
do
   /usr/bin/java -Xms512m  -Xmx1024m  -classpath /var/lib/cbudgetbatch/server.jar:/var/lib/cbudgetbatch/CbudgetBase-1.0.1-SNAPSHOT.jar:/var/lib/cbudgetbatch/postgresql-42.2.4.jar  cbudgetbatch.BerechnePlanungBatch budget budget budget trigger
   sleep 300
done