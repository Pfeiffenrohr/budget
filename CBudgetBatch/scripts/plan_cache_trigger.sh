#!/bin/bash
cd /var/lib/cbudgetbatch
while [ 1 -eq 1 ]
do
   /usr/bin/java  -classpath /var/lib/cbudgetbatch/server.jar:/var/lib/cbudgetbatch/CbudgetBase-3.0.0.jar:/var/lib/cbudgetbatch/postgresql-42.2.4.jar  cbudgetbatch.BerechnePlanungBatch budget budget $connectstring trigger
   sleep $cacheComputeIntervall
done