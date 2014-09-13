#!/bin/bash

if [ $# -ne 4 ];
then
  echo "Usage ./run-netlib.sh <rowsA> <colsA> <colsB> <numParallel>"
  exit 0
fi

BLAS_PATH=/root/openblas-install/lib 
#BLAS_PATH=/usr/lib64/atlas
#BLAS_PATH=/usr/lib64

for i in `seq 1 $4`
do
  LD_LIBRARY_PATH=$BLAS_PATH ./run-main.sh edu.cs.berkeley.amplab.LocalMMNetlib $1 $2 $3 &
done

wait
