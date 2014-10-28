#!/bin/bash

set -e

# Build BLAS for this machine
pushd /root
rm -rf /root/OpenBLAS
git clone https://github.com/xianyi/OpenBLAS.git

pushd /root/OpenBLAS
  make clean
  make -j4

  rm -rf /root/openblas-install
  make install PREFIX=/root/openblas-install
popd

# Create some symlinks
ln -s /root/openblas-install/lib/libopenblas.so /root/openblas-install/lib/libblas.so
ln -s /root/openblas-install/lib/libopenblas.so /root/openblas-install/lib/libblas.so.3

echo "OpenBlas installed to: /root/openblas-install/lib."
