package com.extempo.typescan.model.database

import android.content.Context
import androidx.room.*
import com.extempo.typescan.model.Author
import com.extempo.typescan.utilities.Converters

@Database(entities = [Author::class], version = 1)
@TypeConverters(Converters::class)
abstract class AuthorDatabase: RoomDatabase() {

    abstract fun authorDao(): AuthorDao

    companion object {
        @Volatile private var INSTANCE: AuthorDatabase? = null

        fun getInstance(context: Context): AuthorDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                AuthorDatabase::class.java, "Authors.db")
                .build()
    }
}