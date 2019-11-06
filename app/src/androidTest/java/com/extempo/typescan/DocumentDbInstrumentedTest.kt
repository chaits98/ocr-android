package com.extempo.typescan

import androidx.test.*
import androidx.test.runner.*
import com.extempo.typescan.model.database.DocumentDatabase

import org.junit.Test

import org.junit.runner.RunWith

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
        val docs = DocumentDatabase.getInstance(context).documentItemDao().getAllDocumentItems()
        println(docs)
    }
}
