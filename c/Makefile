.PHONY : clean all

all: local_mm local_mv

CC = gcc

BLAS_DIR = # Set this appropriately
LAPACK_DIR = # Set this appropriately

CFLAGS = -O2 -Wall -Wextra 

LD_FLAGS = -lm -lblas -L$(BLAS_DIR) -Wl,-rpath $(BLAS_DIR) -llapack -L$(LAPACK_DIR) -Wl,-rpath $(LAPACK_DIR)

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
