#!/bin/bash

# Start the first process
/var/lib/cbudgetbatch/budgetserver.sh -D &
status=$?
if [ $status -ne 0 ]; then
  echo "Failed to start my_first_process: $status"
  exit $status
fi

# Start the second process
/var/lib/cbudgetbatch/plan_cache_trigger.sh -D &
status=$?
if [ $status -ne 0 ]; then
  echo "Failed to start my_second_process: $status"
  exit $status
fi

 #Start the 3rd process
/var/lib/cbudgetbatch/forecast.sh -D &
status=$?
if [ $status -ne 0 ]; then
  echo "Failed to start my_third_process: $status"
  exit $status
fi

 #Start the 4rd process
/var/lib/cbudgetbatch/deleteOldHistoryFiles.sh -D &
status=$?
if [ $status -ne 0 ]; then
  echo "Failed to start deleteOldHistoryFiles: $status"
  exit $status
fi

 #Start the 5th process
/var/lib/cbudgetbatch/forecast-evaluation.sh -D &
status=$?
if [ $status -ne 0 ]; then
  echo "Failed to start forecast-evaluation: $status"
  exit $status
fi

# Naive check runs checks once a minute to see if either of the processes exited.
# This illustrates part of the heavy lifting you need to do if you want to run
# more than one service in a container. The container exits with an error
# if it detects that either of the processes has exited.
# Otherwise it loops forever, waking up every 60 seconds

while sleep 60; do
  ps aux |grep budgetserver |grep -q -v grep
  PROCESS_1_STATUS=$?
  ps aux |grep plan_cache_trigger |grep -q -v grep
  PROCESS_2_STATUS=$?
  ps aux |grep forecast.sh |grep -q -v grep
  PROCESS_3_STATUS=$?
  ps aux |grep delete |grep -q -v grep
  PROCESS_4_STATUS=$?
  # "forecast.sh" statt "forecast", sonst matcht der Treffer auch
  # forecast-evaluation.sh und der Prozess waere nie als ausgefallen erkennbar.
  ps aux |grep forecast-evaluation |grep -q -v grep
  PROCESS_5_STATUS=$?
  # If the greps above find anything, they exit with 0 status
  # If they are not both 0, then something is wrong
  if [ $PROCESS_1_STATUS -ne 0 -o $PROCESS_2_STATUS -ne 0 ]; then
    echo "One of the processes has already exited."
    exit 1
  fi
  if [ $PROCESS_3_STATUS -ne 0 -o $PROCESS_4_STATUS -ne 0 ]; then
    echo "One of the processes has already exited."
    exit 1
  fi
  if [ $PROCESS_5_STATUS -ne 0 ]; then
    echo "forecast-evaluation has already exited."
    exit 1
  fi
done

