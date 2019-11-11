package com.extempo.opticalcharacterrecognizer.utilities

import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.imgproc.Imgproc

object ImageDifference {
    fun compareMat(a: Mat, b: Mat): Double {
        var output = 0.0

        val aMinusB = Mat()
        Core.absdiff(a, b, aMinusB)
        val sumA = Core.sumElems(a).`val`
        val sumAMinusB = Core.sumElems(aMinusB).`val`
        var sa = 0.0
        var samb = 0.0

        for (x in sumA) {
            sa += x
        }
        for (x in sumAMinusB) {
            samb += x
        }

        output = (sa - samb) / 255.0

        return output
    }
}