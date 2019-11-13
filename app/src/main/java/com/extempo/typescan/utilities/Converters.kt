package com.extempo.typescan.utilities

import android.util.Base64
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import org.opencv.core.Mat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Converters {
    @TypeConverter
    fun fromString(value: String?): HashMap<String, ArrayList<Mat>>? {
        val mapType = object : TypeToken<HashMap<String, ArrayList<String>>>(){}.type
        val dataMap: HashMap<String, ArrayList<String>> = Gson().fromJson(value, mapType)
        val output: HashMap<String, ArrayList<Mat>>? = HashMap()

        for ((key, value) in dataMap) {
            val list = ArrayList<Mat>()
            value.forEach { list.add(matFromJson(it)) }
            output?.set(key, list)
        }

        return output
    }

    @TypeConverter
    fun fromHashMap(charMap: HashMap<String, ArrayList<Mat>>?): String? {
        val gson = Gson()
        val map = HashMap<String, ArrayList<String>>()

        if (charMap != null) {
            for ((key, value) in charMap) {
                val list = ArrayList<String>()
                value.forEach { list.add(matToJson(it)) }
                map[key] = list
            }
        }

        return gson.toJson(map)
    }

    private fun matToJson(mat: Mat): String {
        val obj = JsonObject()

        if (mat.isContinuous) {
            val cols = mat.cols()
            val rows = mat.rows()
            val elemSize = mat.elemSize().toInt()

            val data = ByteArray(cols * rows * elemSize)

            mat.get(0, 0, data)

            obj.addProperty("rows", mat.rows())
            obj.addProperty("cols", mat.cols())
            obj.addProperty("type", mat.type())

            val dataString = String(Base64.encode(data, Base64.DEFAULT))

            obj.addProperty("data", dataString)

            val gson = Gson()

            return gson.toJson(obj)
        } else {
            println("Mat not continuous.")
        }
        return "{}"
    }

    private fun matFromJson(json: String): Mat {
        val parser = JsonParser()
        val JsonObject = parser.parse(json).asJsonObject

        val rows = JsonObject.get("rows").asInt
        val cols = JsonObject.get("cols").asInt
        val type = JsonObject.get("type").asInt

        val dataString = JsonObject.get("data").asString
        val data = Base64.decode(dataString.toByteArray(), Base64.DEFAULT)

        val mat = Mat(rows, cols, type)
        mat.put(0, 0, data)

        return mat
    }
}