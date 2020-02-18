#!/bin/bash
cd /var/lib/cbudgetbatch
while [ 1 -eq 1 ]
do
    if [ $ComputeOnlyNight != "Yes" ]; then
    	/usr/bin/java  -classpath /var/lib/cbudgetbatch/server.jar:/var/lib/cbudgetbatch/CbudgetBase-3.0.0.jar:/var/lib/cbudgetbatch/postgresql-42.2.4.jar  cbudgetbatch.BerechnePlanungBatch budget budget $connectstring trigger
    else
	    if [ `date +%H` -lt 21 ] && [ `date +%H` -gt 07 ]; then
	        	echo "Time out of Range"
	    else    	
	   		/usr/bin/java  -classpath /var/lib/cbudgetbatch/server.jar:/var/lib/cbudgetbatch/CbudgetBase-3.0.0.jar:/var/lib/cbudgetbatch/postgresql-42.2.4.jar  cbudgetbatch.BerechnePlanungBatch budget budget $connectstring trigger
	   	fi	
	fi     
    sleep $cacheComputeIntervall
done