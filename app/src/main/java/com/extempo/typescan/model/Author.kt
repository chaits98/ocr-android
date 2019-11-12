package com.extempo.typescan.model

import androidx.room.*
import com.extempo.opticalcharacterrecognizer.model.CharacterMap
import com.extempo.opticalcharacterrecognizer.utilities.ImageDifference
import com.extempo.typescan.utilities.CharacterMapFactory
import org.opencv.core.Mat

@Entity(tableName = "Authors")
class Author(var name: String) {
    @PrimaryKey(autoGenerate = true) var id: Long = 0
    var charactermap: HashMap<String, ArrayList<Mat>> = CharacterMapFactory.initCharMap()

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