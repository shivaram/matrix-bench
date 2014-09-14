#!/bin/bash

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

# Build JBLAS for this machine
rm -rf /root/jblas
git clone https://github.com/mikiobraun/jblas.git
pushd /root/jblas

  git checkout v1.2.3
  # Get Evan's openblas patch
  wget https://github.com/mikiobraun/jblas/pull/51.patch
  yum install -y patch
  cat 51.patch | patch -p1
  make clean
  ./configure --static-libs --libpath="/root/openblas-install/lib/" --lapack-build --download-lapack
  make
  mkdir -p /root/matrix-bench/scala/lib/ 
  cp src/main/resources/lib/static/Linux/amd64/sse3/libjblas.so /root/matrix-bench/scala/lib/

popd

# lapack
yum install -y lapack-devel
