#!/bin/bash
cd /var/lib/cbudgetbatch
# Ohne gesetzte Variable wuerde "sleep" ohne Argument sofort mit Fehler
# enden und die Schleife in Sekundenfristen erneut laufen.
: ${evaluationComputeIntervall:=86400}
while [ 1 -eq 1 ]
do
   /usr/bin/java  -classpath /var/lib/cbudgetbatch/server.jar:/var/lib/cbudgetbatch/budget-Version3.jar:/var/lib/cbudgetbatch/postgresql-42.2.4.jar  cbudgetbatch.evaluation.ForecastEvaluation budget budget $connectstring
   sleep $evaluationComputeIntervall
done