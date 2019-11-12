package com.extempo.typescan.utilities

import android.app.Activity
import org.opencv.core.Mat
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

object CharacterMapFactory {
    @Throws(IOException::class)
    public fun initCharMap(): HashMap<String, ArrayList<Mat>> {
        val labels = hashMapOf(
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
        return labels
    }
}