package edu.cs.berkeley.amplab

import org.jblas._
import java.util.concurrent._

object LocalMMJBlas {
  def genData(numRows: Int, numCols: Int, numClasses: Int): (DoubleMatrix, DoubleMatrix) = {
    val data = new Array[Double](numRows * numCols)
    val dataB = new Array[Double](numCols * numClasses)

    var i = 0
    while (i < numRows * numCols) {
      data(i) = ThreadLocalRandom.current().nextGaussian()
      i = i + 1
    }

    i = 0
    while (i < numCols * numClasses) {
      dataB(i) = ThreadLocalRandom.current().nextDouble()
      i = i + 1
    }

    val matA = new DoubleMatrix(numRows, numCols, data:_*)
    val matB = new DoubleMatrix(numCols, numClasses, dataB:_*)

    (matA, matB)
  }

  def runMul(mat: DoubleMatrix, matB: DoubleMatrix): Long = {
    val create = System.nanoTime
    val r = mat.mmul(matB)
    val end = System.nanoTime
    println("Multiply took " + (end - create)/1000000.0 + " ms")
    (end - create)
  }

  def main(args: Array[String]) {

    if (args.length < 3) {
      println("Usage LocalMMJBlas <rowsA> <colsA> <colsB>")
      System.exit(0)
    }

    val numRows = args(0).toInt
    val numCols = args(1).toInt
    val numClasses = args(2).toInt

    val a = new Array[Double](1)

    // Warm up dgemm
    NativeBlas.dgemm('N', 'N', 1, 1, 1, 1.0, a, 0, 1, a, 0, 1, 1.0, a, 0, 1)

    val (aMat, bMat) =  genData(numRows, numCols, numClasses)
    runMul(aMat, bMat)
  }
}
