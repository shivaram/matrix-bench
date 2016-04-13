package edu.cs.berkeley.amplab

import breeze.linalg._

import net.jafama.FastMath

object CCBench {

  case class CCData(
    imgMat: Seq[DenseMatrix[Double]],
    whitenerMat: DenseMatrix[Double],
    whitenerMeans: DenseVector[Double],
    whitenerOffset: Double,
    convolutions: DenseMatrix[Double],
    phase: DenseVector[Double])

  def genData(
      imgRows: Int,
      imgCols: Int,
      numPatchSizeChannels: Int,
      numFilters: Int,
      numImgs: Int): CCData  = {
    val imgMat = (0 until numImgs).map { x => 
      DenseMatrix.rand(imgRows * imgCols, numPatchSizeChannels)
    }
    val whitenerMat = DenseMatrix.rand(numPatchSizeChannels, numPatchSizeChannels)
    val whitenerMeans = DenseVector.rand(numPatchSizeChannels)
    val whitenerOffset = 0.12312
    val convolutions = DenseMatrix.rand(numPatchSizeChannels, numFilters)
    val phase = DenseVector.rand(numFilters)
    CCData(imgMat, whitenerMat, whitenerMeans, whitenerOffset, convolutions, phase)
  }

  def benchCC(ccData: CCData) {
    ccData.imgMat.foreach { imgMat =>
      val whiteningStart = System.nanoTime()
      val W = ccData.whitenerMat
      val means = ccData.whitenerMeans
      imgMat(*,::) :-= means
      val whitenedImage = imgMat * W
      val whiteningTime = timeElapsed(whiteningStart)
     
      val normStart = System.nanoTime()
      whitenedImage :+= ccData.whitenerOffset
      l2Normalize(whitenedImage)
      val normTime = timeElapsed(normStart)
     
      val dgemmStart = System.nanoTime()
      val convRes = whitenedImage * ccData.convolutions
     
      val dgemmTime = timeElapsed(dgemmStart)
     
      val cosStart = System.nanoTime()
      var j = 0
      while (j < convRes.cols) {
        var i = 0
        val pj = ccData.phase(j)
        while (i < convRes.rows) {
          convRes(i,j) = FastMath.cos(convRes(i,j) + pj)
          i += 1
        }
        j += 1
      }
      val cosTime = timeElapsed(cosStart)
     
      System.out.println(
        s"W: ${whiteningTime}, N: ${normTime}, D: ${dgemmTime}, C: ${cosTime}, " +
        s"TOT: ${timeElapsed(whiteningStart)}")
    }
  }

  def l2Normalize(X: DenseMatrix[Double]) {
    var i = 0
    while (i < X.rows) {
      var j = 0
      var norm = 0.0
      while (j < X.cols) {
        norm += X(i,j)*X(i,j)
        j += 1
      }
      norm = FastMath.sqrt(norm)
      while (j < X.cols) {
        X(i,j) = X(i,j)/norm
        j += 1
      }
      i += 1
    }
  }

  def timeElapsed(ns: Long) : Double = (System.nanoTime - ns).toDouble / 1e6


  def main(args: Array[String]) {

    if (args.length < 5) {
      println("Usage: CCBench <imgRows> <imgCols> <patchChannels> <numFilters> <numImgs>")
      System.exit(0)
    }

    val numRows = args(0).toInt
    val numCols = args(1).toInt
    val numChannels = args(2).toInt
    val numFilters = args(3).toInt
    val numImgs = args(4).toInt

    // Warm up breeze ?
    val a = DenseMatrix.rand(2, 2)
    a*a

    val data = genData(numRows, numCols, numChannels, numFilters, numImgs)
    benchCC(data)
  }
}
