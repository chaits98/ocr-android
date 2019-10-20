package com.extempo.typescan.model

import androidx.databinding.*
import androidx.room.*
import java.util.*

@Entity(tableName = "Documents")
data class DocumentItem(
    @Bindable @PrimaryKey val id: String,
    @Bindable val name: String,
    @Bindable val author: String,
    @Bindable val date: Date
): BaseObservable()

