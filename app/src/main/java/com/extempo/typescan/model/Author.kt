package com.extempo.typescan.model

import android.app.Activity
import androidx.databinding.Bindable
import androidx.room.*
import com.extempo.typescan.utilities.CharacterMapFactory
import com.extempo.typescan.utilities.Converters
import org.opencv.core.Mat

@Entity(tableName = "Authors")
class Author(var name: String) {
    @PrimaryKey(autoGenerate = true) var id: Long = 0
    var charactermap: HashMap<Char, ArrayList<Mat>?>? = null

    init {

    }

    fun initCharMap(activity: Activity) {
        charactermap = CharacterMapFactory.initCharMap(activity)
    }
}