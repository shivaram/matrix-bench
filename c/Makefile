.PHONY : clean all

all: local_mm local_mv

CC = gcc

BLAS_DIR = /root/openblas-install/lib # Set this appropriately
#BLAS_DIR = /usr/lib64

CFLAGS = -O2 -Wall -Wextra 

LD_FLAGS = -lm -lblas -L$(BLAS_DIR) -Wl,-rpath $(BLAS_DIR)

local_mm : local_mm.o
	$(CC) -o $@ $^ $(LD_FLAGS)

local_mm.o : local_mm.c
	$(CC) $(CFLAGS) -o $@ -c $<

local_mv : local_mv.o
	$(CC) -o $@ $^ $(LD_FLAGS)

local_mv.o : local_mv.c
	$(CC) $(CFLAGS) -o $@ -c $<

clean :
	rm -f local_mm local_mv
	rm -f *.o
