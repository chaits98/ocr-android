package com.extempo.typescan.model.database

import android.content.Context
import androidx.room.*
import com.extempo.typescan.model.DocumentItem

@Database(entities = arrayOf(DocumentItem::class), version = 1)
abstract class DocumentDatabase: RoomDatabase() {

    abstract fun documentItemDao(): DocumentItemDao

    companion object {
        @Volatile private var INSTANCE: DocumentDatabase? = null

        fun getInstance(context: Context): DocumentDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                DocumentDatabase::class.java, "Documents.db")
                .build()
    }
}