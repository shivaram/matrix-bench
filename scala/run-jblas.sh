#!/bin/bash

if [ $# -ne 4 ];
then
  echo "Usage ./run.sh <rowsA> <colsA> <colsB> <numParallel>"
  exit 0
fi

for i in `seq 1 $4`
do
  ./run-main.sh edu.cs.berkeley.amplab.LocalMMJBlas $1 $2 $3 &
done

wait
