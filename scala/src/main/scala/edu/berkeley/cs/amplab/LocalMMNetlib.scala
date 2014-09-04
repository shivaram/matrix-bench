package edu.cs.berkeley.amplab

import breeze.linalg._

object LocalMMNetlib {
  def genData(numRows: Int, numCols: Int, numClasses: Int): (DenseMatrix[Double], DenseMatrix[Double]) = {
    val matA = DenseMatrix.rand(numRows, numCols)
    val matB = DenseMatrix.rand(numCols, numClasses)
    (matA, matB)
  }

  def runMul(mat: DenseMatrix[Double], matB: DenseMatrix[Double]): Long = {
    val create = System.nanoTime
    val r = mat * matB
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

    // Warm up breeze ?
    val a = DenseMatrix.rand(2, 2)
    a*a

    val (aMat, bMat) =  genData(numRows, numCols, numClasses)
    runMul(aMat, bMat)
  }
}
