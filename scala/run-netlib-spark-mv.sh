#!/bin/bash

if [ $# -ne 4 ];
then
  echo "Usage ./run-netlib-spark-mv.sh <master> <rowsA> <colsA> <numParallels>"
  exit 0
fi

BLAS_PATH=/root/openblas-install/lib 
#BLAS_PATH=/usr/lib64/atlas

LD_LIBRARY_PATH=$BLAS_PATH ./run-main.sh edu.cs.berkeley.amplab.SparkMvNetlib $1 $2 $3 $4

wait
