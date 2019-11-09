package com.extempo.typescan.utilities

import android.app.Activity
import org.opencv.core.Mat
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

object CharacterMapFactory {
    @Throws(IOException::class)
    public fun initCharMap(activity: Activity): HashMap<Char, ArrayList<Mat>?> {
        val labels: HashMap<Char, ArrayList<Mat>?> = HashMap()
        val reader = BufferedReader(InputStreamReader(activity.assets.open(getLabelPath())))
        var line: String
        val iterator = reader.lineSequence().iterator()
        while (iterator.hasNext()) {
            line = iterator.next()
            labels[line[0]] = null
        }
        reader.close()
        return labels
    }

    private fun getLabelPath(): String {
        return "mapping.txt"
    }
}