#!/bin/bash

if [ $# -ne 3 ];
then
  echo "Usage ./run-c-qr.sh <rowsA> <colsA> <numParallel>"
  exit 0
fi

for i in `seq 1 $3`
do
  ./local_qr $1 $2 &
done

wait
