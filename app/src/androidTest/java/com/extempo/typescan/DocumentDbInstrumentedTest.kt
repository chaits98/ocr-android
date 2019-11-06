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

//    @Test
//    fun insertDocumentItemSavesAndRetrievesData() {
//        val doc = DocumentFactory.makeDocumentItem()
//        println("Generated Document Item doc: $doc")
//        println("Generated Document Item doc timestamp: " + doc.timestamp)
//        DocumentDatabase.getInstance(context).documentItemDao().insertDocument(doc)
//        var docsdb = DocumentDatabase.getInstance(context).documentItemDao().getAllDocumentItems()
//        var res = DocumentDatabase.getInstance(context).documentItemDao().getDocumentItemById(doc.id)
//        println("Generated Document Item res: $res")
//        println("Generated document list: $docsdb")
//        assertThat(res[0], equalTo(doc))
//    }

//    @Test
//    fun deleteDocumentItemsClearsData() {
//        val docsdb = DocumentDatabase.getInstance(context).documentItemDao().getAllDocumentItems()
//        docsdb.forEach {
//            DocumentDatabase.getInstance(context).documentItemDao().deleteDocumentItem(it)
//        }
//        assert(docsdb.isNullOrEmpty())
//    }

    @Test
    fun getAllDocumentItemsRetrievesData() {
        val docs = DocumentFactory.makeDocumentList(10)
        for (doc in docs) {
            DocumentDatabase.getInstance(context).documentItemDao().insertDocument(doc)
        }
        var docsdb = DocumentDatabase.getInstance(context).documentItemDao().getAllDocumentItems()
        assert(docsdb.isNotEmpty())
    }

    @Test
    fun deleteAllDocumentItemsClearsData() {
        DocumentDatabase.getInstance(context).documentItemDao().deleteAllDocumentItems()
        val docsdb = DocumentDatabase.getInstance(context).documentItemDao().getAllDocumentItems()
        assert(docsdb.isNullOrEmpty())
    }
}
