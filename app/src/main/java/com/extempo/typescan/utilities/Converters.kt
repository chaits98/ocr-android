package com.extempo.typescan.utilities

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.opencv.core.Mat
import java.util.*
import kotlin.collections.HashMap

class Converters {
    @TypeConverter
    fun fromString(value: String): HashMap<Char, ArrayList<Mat>?>? {
        val mapType = object : TypeToken<HashMap<Char, ArrayList<Mat>?>?>() {}.type
        return Gson().fromJson(value, mapType)
    }

    @TypeConverter
    fun fromHashMap(charMap: HashMap<Char, ArrayList<Mat>?>?): String {
        val gson = Gson()
        return gson.toJson(charMap)
    }
}