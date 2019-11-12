package com.extempo.opticalcharacterrecognizer.model

import com.extempo.opticalcharacterrecognizer.utilities.ImageDifference
import org.opencv.core.Mat

class CharacterMap(val author: String) {
    val dataMap = hashMapOf(
        "0" to ArrayList<Mat>(),
        "1" to ArrayList<Mat>(),
        "2" to ArrayList<Mat>(),
        "3" to ArrayList<Mat>(),
        "4" to ArrayList<Mat>(),
        "5" to ArrayList<Mat>(),
        "6" to ArrayList<Mat>(),
        "7" to ArrayList<Mat>(),
        "8" to ArrayList<Mat>(),
        "9" to ArrayList<Mat>(),
        "a" to ArrayList<Mat>(),
        "b" to ArrayList<Mat>(),
        "d" to ArrayList<Mat>(),
        "e" to ArrayList<Mat>(),
        "f" to ArrayList<Mat>(),
        "g" to ArrayList<Mat>(),
        "h" to ArrayList<Mat>(),
        "n" to ArrayList<Mat>(),
        "q" to ArrayList<Mat>(),
        "r" to ArrayList<Mat>(),
        "t" to ArrayList<Mat>(),
        "A" to ArrayList<Mat>(),
        "B" to ArrayList<Mat>(),
        "C" to ArrayList<Mat>(),
        "D" to ArrayList<Mat>(),
        "E" to ArrayList<Mat>(),
        "F" to ArrayList<Mat>(),
        "G" to ArrayList<Mat>(),
        "H" to ArrayList<Mat>(),
        "I" to ArrayList<Mat>(),
        "J" to ArrayList<Mat>(),
        "K" to ArrayList<Mat>(),
        "L" to ArrayList<Mat>(),
        "M" to ArrayList<Mat>(),
        "N" to ArrayList<Mat>(),
        "O" to ArrayList<Mat>(),
        "P" to ArrayList<Mat>(),
        "Q" to ArrayList<Mat>(),
        "R" to ArrayList<Mat>(),
        "S" to ArrayList<Mat>(),
        "T" to ArrayList<Mat>(),
        "U" to ArrayList<Mat>(),
        "V" to ArrayList<Mat>(),
        "W" to ArrayList<Mat>(),
        "X" to ArrayList<Mat>(),
        "Y" to ArrayList<Mat>(),
        "Z" to ArrayList<Mat>()
    )

    fun compare(characterMap: CharacterMap): Double {
        var overallMetric = 0.0
        var comp = 0
        for ((char, list) in dataMap) {
            if (list.isEmpty() || characterMap.dataMap[char]?.isEmpty()!!) {
                continue
            }
            comp++
            var upper = 0.0
            for (thisMat in list) {
                var lower = 0.0
                characterMap.dataMap[char]?.forEach { toCompareMat -> lower += ImageDifference.compareMat(thisMat, toCompareMat) }
                lower /= characterMap.dataMap[char]?.size!!
                upper += lower
            }
            upper /= list.size
            overallMetric += upper
        }
        overallMetric /= comp
        return overallMetric
    }
}