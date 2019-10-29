package com.extempo.typescan.model

import androidx.databinding.*
import androidx.room.*

@Entity(tableName = "Documents")
data class DocumentItem(
    @Bindable var name: String,
    @Bindable var author: String
): BaseObservable() {
    @Bindable @PrimaryKey(autoGenerate = true) var id: Long = 0
    @Bindable var timestamp: Long = System.currentTimeMillis()
}

