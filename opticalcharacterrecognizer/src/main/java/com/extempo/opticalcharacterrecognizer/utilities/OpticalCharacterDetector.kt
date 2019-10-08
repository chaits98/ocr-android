package com.extempo.opticalcharacterrecognizer.utilities

import android.graphics.Bitmap
import android.util.Log
import com.extempo.opticalcharacterrecognizer.model.listeners.InferenceListener
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import kotlin.Comparator
import kotlin.collections.ArrayList


object OpticalCharacterDetector {
    @Throws(Exception::class)
//    fun findAlphabets(bitmap: Bitmap, inferenceListener: InferenceListener) {
    fun findAlphabets(bitmap: Bitmap, inferenceListener: InferenceListener) {
        var alphabetList: List<Char> = emptyList()

        inferenceListener.started(bitmap)

        var tempMat = Mat()
        var source = Mat()
        Utils.bitmapToMat(bitmap, tempMat)
        Imgproc.cvtColor(tempMat, source, Imgproc.COLOR_BGR2GRAY)
        val result = Mat()
        Imgproc.adaptiveThreshold(
            source,
            result,
            255.0,
            Imgproc.ADAPTIVE_THRESH_MEAN_C,
            Imgproc.THRESH_BINARY_INV,
            15,
            30.0
        )

        var contours = ArrayList<MatOfPoint>()
        Imgproc.findContours(result, contours, Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE)

        for (cnt in contours) {
            val rect = Imgproc.boundingRect(cnt)
//            if (rect.width <= rect.height*1.2 && Imgproc.contourArea(cnt) >= rect.area()*0.5) { //this comparison will be used for each letter after segmentation
                Log.d("log_tag", rect.height.toString() + "     " + rect.width)
                Imgproc.rectangle(result, rect.tl(), rect.br(), Scalar(255.0, 0.0, 255.0))
//                Imgproc.drawContours(result, contours, contours.indexOf(cnt), Scalar(100.0, 0.0, 0.0, 0.8), 2)
//            }
        }

        val bmp = Bitmap.createBitmap(
            result.cols(), result.rows(),
            Bitmap.Config.ARGB_8888
        )
        Utils.matToBitmap(result, bmp)
        inferenceListener.finished(bmp)
//        return alphabetList
    }

    private fun sortArea(contours: ArrayList<MatOfPoint>): ArrayList<MatOfPoint> {
        contours.sortWith(Comparator { o1, o2 ->
            -1 * Imgproc.contourArea(o1).compareTo(Imgproc.contourArea(o2))
        })
        return contours
    }

    private fun sortTopToBottom(contours: ArrayList<MatOfPoint>): ArrayList<MatOfPoint> {
        contours.sortWith(Comparator { o1, o2 ->
            val rect1 = Imgproc.boundingRect(o1)
            val rect2 = Imgproc.boundingRect(o2)
            rect1.tl().y.compareTo(rect2.tl().y)
        })
        return contours
    }

    private fun sortLeftToRight(contours: ArrayList<MatOfPoint>): ArrayList<MatOfPoint> {
        contours.sortWith(Comparator { o1, o2 ->
            val rect1 = Imgproc.boundingRect(o1)
            val rect2 = Imgproc.boundingRect(o2)
            var result = 0
            val total = rect1.tl().y / rect2.tl().y
            if (total in 0.9..1.4) {
                result = rect1.tl().x.compareTo(rect2.tl().x)
            }
            result
        })
        return contours
    }
}