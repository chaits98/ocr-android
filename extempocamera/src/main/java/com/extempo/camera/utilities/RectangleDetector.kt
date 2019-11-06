package com.extempo.camera.utilities


import android.graphics.Bitmap
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.imgproc.Imgproc
import java.util.*


object RectangleDetector {

    @Throws(Exception::class)
    fun findRectangle(bitmap: Bitmap): List<Point> {
        var tempMat = Mat()
        var source = Mat()
        Utils.bitmapToMat(bitmap, tempMat)

        Imgproc.cvtColor(tempMat, source, Imgproc.COLOR_BGR2GRAY)

        val blurred = source.clone()
        Imgproc.medianBlur(source, blurred, 3)

        var gray = Mat()

        Imgproc.Canny(blurred, gray, 100.0, 200.0)
        Imgproc.dilate(gray, gray, Mat(), Point(-1.0, -1.0))

        val contours = ArrayList<MatOfPoint>()
        Imgproc.findContours(gray, contours, Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE)

        var maxArea = 0.0
        var maxAreaContourID = -1
        var maxAreaApproxCurve = MatOfPoint2f()

        for (cnt in contours) {
            val temp = MatOfPoint2f(*cnt.toArray())
            val area = Imgproc.contourArea(cnt)
            var tempApproxCurve = MatOfPoint2f()
            Imgproc.approxPolyDP(
                temp, tempApproxCurve,
                Imgproc.arcLength(temp, true) * 0.02, true
            )
            if (tempApproxCurve.total() == 4L && area >= maxArea) {
                maxArea = area
                maxAreaContourID = contours.indexOf(cnt)
                maxAreaApproxCurve = tempApproxCurve
            }
        }


        if (maxAreaContourID > -1) {
//            Imgproc.drawContours(gray, contours, maxAreaContourID, Scalar(100.0, 0.0, 0.0, 0.8), 5)
//            val bmp = Bitmap.createBitmap(
//                gray.cols(), gray.rows(),
//                Bitmap.Config.ARGB_8888
//            )
//            Utils.matToBitmap(gray, bmp)
//            binding.imageBitmap = bmp

            return listOf(
                Point(maxAreaApproxCurve.get(0,0)),
                Point(maxAreaApproxCurve.get(1,0)),
                Point(maxAreaApproxCurve.get(2,0)),
                Point(maxAreaApproxCurve.get(3,0))
            )
        }

        return emptyList()
    }
}