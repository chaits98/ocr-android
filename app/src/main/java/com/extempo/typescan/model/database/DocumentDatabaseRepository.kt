package com.extempo.typescan.model.database

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.paging.Config
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.extempo.typescan.model.DocumentItem
import com.extempo.typescan.utilities.ThreadManagement

object DocumentDatabaseRepository {

    @WorkerThread
    fun getAllDocumentItems(context: Context): LiveData<PagedList<DocumentItem>> {
        return DocumentDatabase.getInstance(context).documentItemDao().getAllDocumentItems().toLiveData(
            Config(pageSize = 30, enablePlaceholders = true, maxSize = 100), boundaryCallback = null)
    }

    @WorkerThread
    fun insertDocumentItem(documentItem: DocumentItem, data: ArrayList<String>, context: Context) {
        ThreadManagement.databaseExecutor.execute {
            documentItem.generateOutputFile(context, data)
            DocumentDatabase.getInstance(context).documentItemDao().insertDocument(documentItem)
        }
    }

    @WorkerThread
    fun updateDocumentItem(documentItem: DocumentItem, context: Context) {
        ThreadManagement.databaseExecutor.execute {
            DocumentDatabase.getInstance(context).documentItemDao().updateDocumentItem(documentItem)
        }
    }

    @WorkerThread
    fun deleteDocumentItem(documentItem: DocumentItem, context: Context) {
        ThreadManagement.databaseExecutor.execute {
            DocumentDatabase.getInstance(context).documentItemDao().deleteDocumentItem(documentItem)
        }
    }

    @WorkerThread
    fun deleteAllDocumentItems(context: Context) {
        ThreadManagement.databaseExecutor.execute {
            DocumentDatabase.getInstance(context).documentItemDao().deleteAllDocumentItems()
        }
    }
}
