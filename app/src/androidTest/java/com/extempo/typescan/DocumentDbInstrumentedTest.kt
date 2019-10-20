package com.extempo.typescan

import android.app.Application
import android.content.Context
import androidx.room.*
import androidx.test.*
import androidx.test.runner.*
import com.extempo.typescan.model.database.DocumentDatabase
import com.extempo.typescan.model.database.DocumentItemDao

import org.junit.Test

import org.junit.Assert.*
import org.junit.*
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class DocumentDbInstrumentedTest {
    private lateinit var documentItemDao: DocumentItemDao
    private lateinit var db: DocumentDatabase

    @Before
    fun createDB() {
        val context = InstrumentationRegistry.getContext()
        db = Room.inMemoryDatabaseBuilder(
            context, DocumentDatabase::class.java).build()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertDocumentItemSavesData() {

    }

    @Test
    fun getAllDocumentItemsRetrievesData() {

    }

    @Test
    fun getDocumentItemByIdRetrievesData() {

    }

    @Test
    fun deleteDocumentItemsClearsData() {

    }
}
