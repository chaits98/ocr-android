package com.extempo.opticalcharacterrecognizer.utilities

import android.graphics.Bitmap
import com.extempo.opticalcharacterrecognizer.model.listeners.InferenceListener
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import java.util.ArrayList

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

        val blurred = source.clone()
        Imgproc.medianBlur(source, blurred, 3)

        var gray = Mat()

        Imgproc.Canny(blurred, gray, 100.0, 200.0)
        Imgproc.dilate(gray, gray, Mat(), org.opencv.core.Point(-1.0, -1.0))

        val contours = ArrayList<MatOfPoint>()
        Imgproc.findContours(gray, contours, Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE)

        for (cnt in contours) {
            Imgproc.drawContours(source, contours, contours.indexOf(cnt), Scalar(100.0, 0.0, 0.0, 0.8), 2)
        }

        val bmp = Bitmap.createBitmap(
            source.cols(), source.rows(),
            Bitmap.Config.ARGB_8888
        )
        Utils.matToBitmap(source, bmp)
        inferenceListener.finished(bmp)
//        return alphabetList
    }
}