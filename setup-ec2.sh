#!/bin/bash

bash ./build-openblas-ec2.sh

bash ./build-jblas-ec2.sh

# lapack
yum install -y lapack-devel
