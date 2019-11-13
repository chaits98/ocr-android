package com.extempo.typescan.model

import android.util.Base64
import androidx.room.*
import com.extempo.opticalcharacterrecognizer.model.CharacterMap
import com.extempo.opticalcharacterrecognizer.utilities.ImageDifference
import com.extempo.typescan.utilities.CharacterMapFactory
import com.extempo.typescan.utilities.Converters
import org.opencv.core.Mat
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.Gson


@Entity(tableName = "Authors")
class Author(var name: String) {
    @PrimaryKey(autoGenerate = true) var id: Long = 0
    @TypeConverters(Converters::class)
    var charactermap: HashMap<String, ArrayList<Mat>> = CharacterMapFactory.initCharMap()

    init {
        println("log_tag: name: " + this.name)
        println("log_tag: id: " + this.id)
        println("log_tag: charmap: " + this.charactermap)
    }

    fun compare(characterMap: CharacterMap): Double {
        var overallMetric = 0.0
        var comp = 0
        for ((char, list) in charactermap) {
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