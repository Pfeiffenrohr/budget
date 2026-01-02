#!/bin/bash
cd /var/lib/cbudgetbatch
while [ 1 -eq 1 ]
do
    if [ $ComputeOnlyNight != "night" ]; then
    	/usr/bin/java  -classpath /var/lib/cbudgetbatch/server.jar:/var/lib/cbudgetbatch/budget-Version3.jar:/var/lib/cbudgetbatch/postgresql-42.2.4.jar  cbudgetbatch.BerechnePlanungBatch budget budget $connectstring trigger
    else
	    if [ `date +%H` -lt 21 ] && [ `date +%H` -gt 07 ]; then
	        	echo "" >/dev/null
	    else    	
	   		/usr/bin/java  -classpath /var/lib/cbudgetbatch/server.jar:/var/lib/cbudgetbatch/budget-Version3.jar:/var/lib/cbudgetbatch/postgresql-42.2.4.jar  cbudgetbatch.BerechnePlanungBatch budget budget $connectstring trigger
	   	fi	
	fi     
    sleep $cacheComputeIntervall
done