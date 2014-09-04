package edu.cs.berkeley.amplab

import org.apache.spark.SparkContext
import org.apache.log4j.Logger
import org.apache.log4j.Level

import org.jblas._
import java.util.concurrent._

object SparkMMJBlas {
  Logger.getLogger("org.apache.spark").setLevel(Level.WARN)

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

    if (args.length < 5) {
      println("Usage SparkMMJBlas <master> <rowsA> <colsA> <colsB> <parts>")
      System.exit(0)
    }

    val master = args(0)
    val numRows = args(1).toInt
    val numCols = args(2).toInt
    val numClasses = args(3).toInt
    val parts = args(4).toInt

    val sc = new SparkContext(master, "SparkMMJBlas")

    sc.parallelize(0 until parts, parts).foreach { x =>
      val a = new Array[Double](1)
      // Warm up dgemm on all machines
      NativeBlas.dgemm('N', 'N', 1, 1, 1, 1.0, a, 0, 1, a, 0, 1, 1.0, a, 0, 1)
    }

    val data = sc.parallelize(0 until parts, parts).map { p =>
      genData(numRows, numCols, numClasses)
    }.cache()
    data.count

    // Run multiply
    data.foreach { case (aMat, bMat) => runMul(aMat, bMat) }
  }
}
