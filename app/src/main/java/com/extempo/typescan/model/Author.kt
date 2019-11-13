package com.extempo.typescan.model

import androidx.room.*
import com.extempo.opticalcharacterrecognizer.utilities.ImageDifference
import com.extempo.typescan.utilities.CharacterMapFactory
import com.extempo.typescan.utilities.Converters
import org.opencv.core.Mat


@Entity(tableName = "Authors")
class Author(var name: String) {
    @PrimaryKey(autoGenerate = true) var id: Long = 0
    @TypeConverters(Converters::class)
    var charactermap: HashMap<String, ArrayList<Mat>> = CharacterMapFactory.initCharMap()

    fun compare(characterMap: HashMap<String, ArrayList<Mat>>): Double {
        var overallMetric = 0.0
        var comp = 0
        for ((char, list) in charactermap) {
            if (list.isEmpty() || characterMap[char]!!.isEmpty()) {
                continue
            }
            comp++
            var upper = 0.0
            for (thisMat in list) {
                var lower = 0.0
                characterMap[char]?.forEach { toCompareMat -> lower += ImageDifference.compareMat(thisMat, toCompareMat) }
                lower /= characterMap[char]?.size!!
                upper += lower
            }
            upper /= list.size
            overallMetric += upper
        }
        overallMetric /= comp
        println("log_tag: overall: $overallMetric")
        return overallMetric
    }
}