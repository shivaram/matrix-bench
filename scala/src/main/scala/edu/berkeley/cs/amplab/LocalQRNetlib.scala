package edu.cs.berkeley.amplab

import breeze.linalg._
import breeze.generic.UFunc
import org.netlib.util.intW
import com.github.fommil.netlib.LAPACK.{getInstance=>lapack}

object LocalQRNetlib {

  /**
   * Compute a QR decomposition. 
   * @returns Y, the householder reflectors
   * @returns T, the scalar factors
   * @returns R, upper triangular
   */
  def qrYTR(A: DenseMatrix[Double], shouldCloneMatrix: Boolean = true) = {
    val m = A.rows
    val n = A.cols

    // Get optimal workspace size
    // we do this by sending -1 as lwork to the lapack function
    val scratch, work = new Array[Double](1)
    val info = new intW(0)
    lapack.dgeqrf(m, n, scratch, m, scratch, work, -1, info)
    val lwork1 = if(info.`val` != 0) n else work(0).toInt
    val workspace = new Array[Double](lwork1)

    // Perform the QR factorization with dgeqrf
    val maxd = scala.math.max(m, n)
    val mind = scala.math.min(m, n)
    val tau = new Array[Double](mind)
    val outputMat = if (shouldCloneMatrix) {
      // cloneMatrix(A)
      A.copy
    } else {
      A
    }
    val lapackCallStart = System.nanoTime
    lapack.dgeqrf(m, n, outputMat.data, m, tau, workspace, workspace.length, info)

    println("LAPACK dgeqrf took " + (System.nanoTime - lapackCallStart)/1e6 + " ms")

    // Error check
    if (info.`val` > 0)
      throw new NotConvergedException(NotConvergedException.Iterations)
    else if (info.`val` < 0)
      throw new IllegalArgumentException()

    // Get R
    val R = DenseMatrix.zeros[Double](mind, n)
    var r = 0
    while (r < mind) {
      var c = r
      while (c < n) {
        R(r, c) = outputMat(r, c)
        c = c + 1
      }
      r = r + 1
    }
    
    (outputMat, tau, R)
  }

  /**
   * Compute R from a reduced or thin QR factorization
   */
  def qrR(A: DenseMatrix[Double], cloneMatrix: Boolean = true) = {
    qrYTR(A, cloneMatrix)._3
  }

  /**
   * Deep copy a Breeze matrix
   */
  def cloneMatrix(in: DenseMatrix[Double]) = {
    // val arrCopy = new Array[Double](in.rows * in.cols)
    // System.arraycopy(in.data, 0, arrCopy, 0, arrCopy.length)
    val out = new DenseMatrix[Double](in.rows, in.cols)
    var r = 0
    while (r < in.rows) {
      var c = 0
      while (c < in.cols) {
        out(r, c) = in(r, c)
        c = c + 1
      }
      r = r + 1
    }
    out
  }

  def genData(numRows: Int, numCols: Int): DenseMatrix[Double] = {
    DenseMatrix.rand(numRows, numCols)
  }

  def runQR(mat: DenseMatrix[Double]): Long = {
    val create = System.nanoTime
    val r = qrR(mat, true)
    val end = System.nanoTime
    println("QR took " + (end - create)/1000000.0 + " ms")
    (end - create)
  }

  def main(args: Array[String]) {

    if (args.length < 2) {
      println("Usage LocalQRNetlib <rowsA> <colsA>")
      System.exit(0)
    }

    val numRows = args(0).toInt
    val numCols = args(1).toInt

    // Warm up breeze ?
    val a = DenseMatrix.rand(2, 2)
    a*a

    val aMat = genData(numRows, numCols)
    runQR(aMat)
  }
}
