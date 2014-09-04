#include <assert.h>
#include <stdlib.h>
#include <stdio.h>
#include <time.h>
#include <sys/time.h>

#include <math.h>

extern void dgemm_( char* transa, char* transb, int* m, int* n, int* k,
                    double* alpha, double* a, int* lda, double* b, int* ldb,
                    double* beta, double* c, int* ldc );

double *allocate_matrix(int rows, int cols) {
  double *mat = NULL;
  mat = malloc(sizeof(double) * rows * cols);
  assert(mat != NULL);
  return (mat);
}

double *random_matrix(int rows, int cols) {

  int r, c;
  double *mat = allocate_matrix(rows, cols);

  /* Iterate over the columns of the matrix */
  for (c = 0; c < cols; c++) {
    /* Iterate over the rows of the matrix */
    for (r = 0; r < rows; r++) {
      int index = (c * rows) + r;
      mat[index] = round(10.0 * rand() / (RAND_MAX + 1.0));
    } /* r */
  } /* c */

  return mat;
}

struct timeval diff(struct timeval start, struct timeval end) {
  struct timeval temp;
  if ((end.tv_usec - start.tv_usec) < 0) {
    temp.tv_sec = end.tv_sec-start.tv_sec-1;
    temp.tv_usec = 1000000L+end.tv_usec-start.tv_usec;
  } else {
    temp.tv_sec = end.tv_sec-start.tv_sec;
    temp.tv_usec = end.tv_usec-start.tv_usec;
  }
  return temp;
}

int main(int argc, char *argv[]) {
  if (argc < 4) {
    printf("Usage: %s <rowsA> <colsA/rowsB> <colsB>\n", argv[0]);
    return 0;
  }
  double *A_block, *B_block, *C_block;
  int rowsA = atoi(argv[1]);
  int colsA = atoi(argv[2]);
  int rowsB = colsA;
  int colsB = atoi(argv[3]);

  A_block = random_matrix(rowsA, colsA);
  B_block = random_matrix(colsA, colsB);
  C_block = random_matrix(rowsA, colsB);
  double one = 1.0;
  
  struct timeval time1, time2;
  gettimeofday(&time1, 0);

  dgemm_("N", "N", &rowsA, &colsB, &colsA, &one,
         A_block, &rowsA, B_block, &rowsB, &one, C_block, &rowsA);
  gettimeofday(&time2, 0);

  struct timeval diff_t = diff(time1, time2);

  printf("%lld.%.6d seconds\n", (long long)diff_t.tv_sec, diff_t.tv_usec);
  return 0;
}
