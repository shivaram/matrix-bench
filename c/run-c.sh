#!/bin/bash

if [ $# -ne 4 ];
then
  echo "Usage ./run-c.sh <rowsA> <colsA> <colsB> <numParallel>"
  exit 0
fi

for i in `seq 1 $4`
do
  ./local_mm $1 $2 $3 &
done

wait
