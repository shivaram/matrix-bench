#include <assert.h>
#include <stdlib.h>
#include <stdio.h>
#include <time.h>
#include <sys/time.h>

#include <math.h>

#define MIN(x, y) ((x) < (y) ? (x) : (y))

extern void dgeqrf_(int *m, int *n, double *a, int * lda,
                    double *tau, double *work, int *lwork, int *info);

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

double timeval_to_ms(struct timeval t) {
  return (((double) t.tv_sec)*1000000.0 + (double) t.tv_usec)/1000.0;
}

void print_matrix(double* a, int rows, int cols) {
  int i, j;
  // Note: Lapack uses column-major order.
  for (i = 0; i < rows; ++i) {
    for (j = 0; j < cols; ++j)
      printf("%.4f ", *(a + cols * j + i));
    printf("\n");
  }
}

int main(int argc, char *argv[]) {
  if (argc < 2) {
    printf("Usage: %s <rowsA> <colsA>\n", argv[0]);
    return 0;
  }
  double *A_block;
  int rowsA = atoi(argv[1]);
  int colsA = atoi(argv[2]);

  A_block = random_matrix(rowsA, colsA);
  /* printf("Generated random matrix A: \n"); */
  /* print_matrix(A_block, rowsA, colsA); */

  struct timeval time1, time2;
  gettimeofday(&time1, 0);

  double* tau = NULL;
  int info = -1, lwork = -1;
  double* work = malloc(sizeof(double));
  tau = malloc(sizeof(double) * MIN(rowsA, colsA));

  dgeqrf_(&rowsA, &colsA, A_block, &rowsA, tau, work, &lwork, &info); // probe optimal work size
  lwork = (int) *work;
  work = malloc(sizeof(double) * lwork);
  dgeqrf_(&rowsA, &colsA, A_block, &rowsA, tau, work, &lwork, &info);

  if (info) {
    printf("ERROR: dgeqrf failed, exit `info` is %d\n", info);
    return 1;
  } else {
    /* printf("QR (dgeqrf) successfully completed.\n"); */

    gettimeofday(&time2, 0);

    struct timeval diff_t = diff(time1, time2);

    printf("%2.6f ms\n", timeval_to_ms(diff_t));
    return 0;
  }

}

