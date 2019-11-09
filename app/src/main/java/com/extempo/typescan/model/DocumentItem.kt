package com.extempo.typescan.model

import android.content.Context
import android.text.format.DateFormat
import androidx.databinding.*
import androidx.room.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@Entity(tableName = "Documents")
data class DocumentItem(
    @Bindable var title: String,
    @Bindable var author: String
): BaseObservable(), Serializable {
    @Bindable @PrimaryKey(autoGenerate = true) var id: Long = 0
    @Bindable var timestamp: Long = System.currentTimeMillis()
    @Bindable var date: String? = null
    @Bindable var filename: String

    init {
        val cal = Calendar.getInstance(Locale.ENGLISH)
        cal.timeInMillis = this.timestamp
        this.date = DateFormat.format("dd MMMM yyyy", cal).toString()
        filename = "tempFile$timestamp"
    }

    fun generateFilename() {
        filename = this.title + timestamp
    }

    fun generateOutputFile(context: Context, data: ArrayList<String>) {
        data.forEach { println("log_tag doc item class $it") }
        this.generateFilename()
        context.filesDir.mkdirs()
        try {
            val file = File(context.filesDir, this.filename + ".txt")
            println("log_tag: ${file.path}")
//            val fileOutputStream = FileOutputStream(file)
//            val outputStreamWriter = OutputStreamWriter(fileOutputStream)
            val outputStreamWriter = FileWriter(file)
            repeat(data.size) {
                outputStreamWriter.append(data[it])
                outputStreamWriter.append("\n\r")
            }
            outputStreamWriter.flush()
            outputStreamWriter.close()
//            fileOutputStream.close()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}

