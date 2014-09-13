package edu.cs.berkeley.amplab

import org.apache.spark.SparkContext
import org.apache.log4j.Logger
import org.apache.log4j.Level

import java.util.concurrent._
import breeze.linalg._

object SparkMvNetlib {
  Logger.getLogger("org.apache.spark").setLevel(Level.WARN)

  def genData(numRows: Int, numCols: Int): (DenseMatrix[Double], DenseVector[Double]) = {
    val matA = DenseMatrix.rand(numRows, numCols)
    val matB = DenseVector.rand(numCols)
    (matA, matB)
  }


  def runMul(mat: DenseMatrix[Double], matB: DenseVector[Double]): Long = {
    val create = System.nanoTime
    val r = mat * matB
    val end = System.nanoTime
    println("Multiply took " + (end - create)/1000000.0 + " ms")
    (end - create)
  }

  def main(args: Array[String]) {

    if (args.length < 4) {
      println("Usage SparkMvNetlib <master> <rowsA> <colsA> <parts>")
      System.exit(0)
    }

    val master = args(0)
    val numRows = args(1).toInt
    val numCols = args(2).toInt
    val parts = args(3).toInt

    val sc = new SparkContext(master, "SparkMvNetlib", System.getenv("SPARK_HOME"), Seq("target/scala-2.10/matrix-bench-assembly-0.1.jar"))

    sc.parallelize(0 until parts, parts).foreach { x =>
      val a = DenseMatrix.rand(2, 2)
      a*a
    }

    val data = sc.parallelize(0 until parts, parts).map { p =>
      genData(numRows, numCols)
    }.cache()
    data.count

    // Run multiply
    data.map { case (aMat, bMat) => runMul(aMat, bMat) }.foreach(t => println("Multiply took " + t/1000000.0 + " ms"))
  }
}
