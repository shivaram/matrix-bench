#!/bin/bash

if [ $# -ne 5 ];
then
  echo "Usage ./run-jblas-spark.sh <master> <rowsA> <colsA> <colsB> <numParallel>"
  exit 0
fi

./run-main.sh edu.cs.berkeley.amplab.SparkMMJBlas $1 $2 $3 $4 $5
