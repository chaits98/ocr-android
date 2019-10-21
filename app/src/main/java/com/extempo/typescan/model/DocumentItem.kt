package com.extempo.typescan.model

import androidx.databinding.*
import androidx.room.*
import java.sql.Timestamp

@Entity(tableName = "Documents")
data class DocumentItem(
    @Bindable var name: String,
    @Bindable var author: String
): BaseObservable() {
    @Bindable @PrimaryKey(autoGenerate = true) var id: Int = 0
    @Bindable var timestamp: Timestamp = Timestamp(System.currentTimeMillis())
}

