package com.extempo.typescan.model

import android.text.format.DateFormat
import androidx.databinding.*
import androidx.room.*
import java.util.*

@Entity(tableName = "Documents")
data class DocumentItem(
    @Bindable var name: String,
    @Bindable var author: String
): BaseObservable() {
    @Bindable @PrimaryKey(autoGenerate = true) var id: Long = 0
    @Bindable var timestamp: Long = System.currentTimeMillis()
    @Bindable var date: String? = null
    init {
        val cal = Calendar.getInstance(Locale.ENGLISH)
        cal.timeInMillis = this.timestamp
        this.date = DateFormat.format("dd-MM-yyyy", cal).toString()
    }
}

