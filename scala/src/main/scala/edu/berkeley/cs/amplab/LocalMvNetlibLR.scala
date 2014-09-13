package edu.cs.berkeley.amplab

import breeze.linalg._
import breeze.numerics._

object LocalMvNetlibLR {
  def genData(numRows: Int, numCols: Int): (DenseMatrix[Double], DenseVector[Double]) = {
    val matA = DenseMatrix.rand(numRows, numCols)
    val vecb = DenseVector.rand(numCols)
    (matA, vecb)
  }

  def runMul(mat: DenseMatrix[Double], vec: DenseVector[Double]): Long = {
    val y = DenseVector.rand(mat.rows)
    val create = System.nanoTime
    val r = mat.t * sigmoid(mat * vec - y)
    val end = System.nanoTime
    println("Multiply took " + (end - create)/1000000.0 + " ms")
    (end - create)
  }

  def main(args: Array[String]) {

    if (args.length < 2) {
      println("Usage LocalMvJBlas <rowsA> <colsA>")
      System.exit(0)
    }

    val numRows = args(0).toInt
    val numCols = args(1).toInt

    // Warm up breeze ?
    val a = DenseMatrix.rand(2, 2)
    a*a

    val (aMat, bvec) =  genData(numRows, numCols)
    runMul(aMat, bvec)
  }
}
