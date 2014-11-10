#!/bin/bash

if [ $# -ne 3 ];
then
  echo "Usage ./run-netlib.sh <rowsA> <colsA> <numParallel>"
  exit 0
fi

BLAS_PATH=/root/openblas-install/lib 
#BLAS_PATH=/usr/lib64/atlas
#BLAS_PATH=/usr/lib64

for i in `seq 1 $3`
do
  LD_LIBRARY_PATH=$BLAS_PATH ./run-main.sh edu.cs.berkeley.amplab.LocalQRNetlib $1 $2 &
done

wait
