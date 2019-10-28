package com.extempo.typescan.model.database

import android.content.Context
import androidx.annotation.WorkerThread
import com.extempo.typescan.model.DocumentItem

object DocumentDatabaseRepository {

    @WorkerThread
    fun getAllDocumentItems(context: Context): List<DocumentItem> {
        return DocumentDatabase.getInstance(context).documentItemDao().getAllDocumentItems()
    }

    @WorkerThread
    fun insertDocumentItem(documentItem: DocumentItem, context: Context) {
        return DocumentDatabase.getInstance(context).documentItemDao().insertDocument(documentItem)
    }

    @WorkerThread
    fun updateDocumentItem(documentItem: DocumentItem, context: Context) {
        return DocumentDatabase.getInstance(context).documentItemDao().updateDocumentItem(documentItem)
    }

    @WorkerThread
    fun deleteDocumentItem(documentItem: DocumentItem, context: Context) {
        return DocumentDatabase.getInstance(context).documentItemDao().deleteDocumentItem(documentItem)
    }

    @WorkerThread
    fun deleteAllDocumentItems(documentItem: DocumentItem, context: Context) {
        return DocumentDatabase.getInstance(context).documentItemDao().deleteAllDocumentItems()
    }
}
