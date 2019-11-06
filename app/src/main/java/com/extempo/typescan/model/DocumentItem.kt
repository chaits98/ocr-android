package com.extempo.typescan.model

import android.content.Context
import android.text.format.DateFormat
import androidx.databinding.*
import androidx.room.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@Entity(tableName = "Documents")
data class DocumentItem(
    @Bindable var title: String,
    @Bindable var author: String
): BaseObservable() {
    @Bindable @PrimaryKey(autoGenerate = true) var id: Long = 0
    @Bindable var timestamp: Long = System.currentTimeMillis()
    @Bindable var date: String? = null
    @Bindable var filename: String

    init {
        val cal = Calendar.getInstance(Locale.ENGLISH)
        cal.timeInMillis = this.timestamp
        this.date = DateFormat.format("dd MMMM yyyy", cal).toString()
        filename = this.title + timestamp
    }

    fun generateOutputFile(context: Context, data: ArrayList<String>) {
        val file = File(context.filesDir, this.filename)
        val fileOutputStream = FileOutputStream(file)
        val outputStreamWriter = OutputStreamWriter(fileOutputStream)

        repeat(data.size) {
            outputStreamWriter.append(data[it])
            outputStreamWriter.append("\n\r")
        }

        outputStreamWriter.close()
        fileOutputStream.close()
    }
}

