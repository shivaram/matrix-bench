package edu.cs.berkeley.amplab

import org.apache.spark.SparkContext
import org.apache.log4j.Logger
import org.apache.log4j.Level

import java.util.concurrent._
import breeze.linalg._

object SparkMMNetlib {
  Logger.getLogger("org.apache.spark").setLevel(Level.WARN)

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

    if (args.length < 5) {
      println("Usage SparkMMNetlib <master> <rowsA> <colsA> <colsB> <parts>")
      System.exit(0)
    }

    val master = args(0)
    val numRows = args(1).toInt
    val numCols = args(2).toInt
    val numClasses = args(3).toInt
    val parts = args(4).toInt

    val sc = new SparkContext(master, "SparkMMNetlib")

    sc.parallelize(0 until parts, parts).foreach { x =>
      val a = DenseMatrix.rand(2, 2)
      a*a
    }

    val data = sc.parallelize(0 until parts, parts).map { p =>
      genData(numRows, numCols, numClasses)
    }.cache()
    data.count

    // Run multiply
    data.foreach { case (aMat, bMat) => runMul(aMat, bMat) }
  }
}
