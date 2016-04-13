#!/bin/bash

if [ $# -ne 6 ];
then
  echo "Usage ./run-netlib.sh <rowsA> <colsA> <patchChannels> <filters> <numImgs> <numParallel>"
  exit 0
fi

#BLAS_PATH=/root/openblas-install/lib 
#BLAS_PATH=/usr/lib64/atlas
BLAS_PATH=/usr/lib

for i in `seq 1 $6`
do
  LD_LIBRARY_PATH=$BLAS_PATH ./run-main.sh edu.cs.berkeley.amplab.CCBench $1 $2 $3 $4 $5 &
done

wait
