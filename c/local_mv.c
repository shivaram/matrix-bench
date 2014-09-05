#include <assert.h>
#include <stdlib.h>
#include <stdio.h>
#include <time.h>
#include <sys/time.h>

#include <math.h>

extern void dgemv_( char* trans, int* m, int* n,
                    double* alpha, double* a, int* lda, double* x, int* incx,
                    double* beta, double* y, int* incy);

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

int main(int argc, char *argv[]) {
  if (argc < 3) {
    printf("Usage: %s <rowsA> <colsA/rowsB>\n", argv[0]);
    return 0;
  }
  double *A_block, *x_block, *y_block;
  int rowsA = atoi(argv[1]);
  int colsA = atoi(argv[2]);
  int rowsx = colsA;
  int rowsy = rowsA;

  A_block = random_matrix(rowsA, colsA);
  x_block = random_matrix(rowsx, 1);
  y_block = random_matrix(rowsy, 1);
  double one = 1.0;
  int incx = 1;
  int incy = 1;
  
  struct timeval time1, time2;
  gettimeofday(&time1, 0);

  dgemv_("N", &rowsA, &colsA, &one,
         A_block, &rowsA, x_block, &incx, &one, y_block, &incy);
  gettimeofday(&time2, 0);

  struct timeval diff_t = diff(time1, time2);

  printf("%2.6f ms\n", timeval_to_ms(diff_t));
  return 0;
}
