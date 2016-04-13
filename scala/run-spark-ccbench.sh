#!/bin/bash

if [ $# -ne 6 ];
then
  echo "Usage ./run-netlib.sh <rowsA> <colsA> <patchChannels> <filters> <numImgs> <numParallel>"
  exit 0
fi

#BLAS_PATH=/root/openblas-install/lib 
#BLAS_PATH=/usr/lib64/atlas
BLAS_PATH=/usr/lib

LD_LIBRARY_PATH=$BLAS_PATH ./run-main.sh edu.cs.berkeley.amplab.SparkCCBench $1 $2 $3 $4 $5 $6 &

wait
