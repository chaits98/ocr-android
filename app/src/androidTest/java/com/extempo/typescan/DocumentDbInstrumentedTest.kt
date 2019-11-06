package com.extempo.typescan

import android.app.Application
import android.content.Context
import androidx.room.*
import androidx.test.*
import androidx.test.runner.*
import com.extempo.typescan.model.database.DocumentDatabase
import com.extempo.typescan.model.database.DocumentItemDao
import org.hamcrest.CoreMatchers.equalTo

import org.junit.Test

import org.junit.Assert.*
import org.junit.*
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DocumentDbInstrumentedTest {
    val context = InstrumentationRegistry.getTargetContext()

    @Test
    fun insertDocumentItemSavesAndRetrievesData() {
        DocumentDatabase.getInstance(context).documentItemDao().deleteAllDocumentItems()
        repeat(10) {
            DocumentDatabase.getInstance(context).documentItemDao()
                .insertDocument(DocumentFactory.makeDocumentItem())
        }
        var docs = DocumentDatabase.getInstance(context).documentItemDao().getAllDocumentItems()
        println(docs)
    }
}
