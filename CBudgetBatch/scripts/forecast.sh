#!/bin/bash
cd /var/lib/cbudgetbatch
while [ 1 -eq 1 ]
do
   sleep $forecastComputeIntervall
   /usr/bin/java  -classpath /var/lib/cbudgetbatch/server.jar:/var/lib/cbudgetbatch/CbudgetBase-3.0.0.jar:/var/lib/cbudgetbatch/postgresql-42.2.4.jar  cbudgetbatch.Forecast budget budget $connectstring
  
done