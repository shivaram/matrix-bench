#!/bin/bash

set -e

# Build JBLAS for this machine
rm -rf /root/jblas
git clone https://github.com/mikiobraun/jblas.git /root/jblas
pushd /root/jblas

  git checkout jblas-1.2.3
  # Get Evan's openblas patch
  wget https://github.com/mikiobraun/jblas/pull/51.patch
  yum install -y patch ant
  cat 51.patch | patch -p1
  ./configure --static-libs --libpath="/root/openblas-install/lib/" --lapack-build --download-lapack
  make
  mkdir -p /root/matrix-bench/scala/lib/
  cp src/main/resources/lib/static/Linux/amd64/sse3/libjblas.so /root/matrix-bench/scala/lib/

popd

echo "JBlas installed to: /root/matrix-bench/scala/lib."
