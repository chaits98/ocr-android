package com.extempo.typescan.model.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.extempo.typescan.model.DocumentItem
import com.extempo.typescan.model.database.DocumentDatabaseRepository

class DocumentRepository(private val context: Context) {

    fun getAllDocumentItems(): LiveData<PagedList<DocumentItem>>? {
        return DocumentDatabaseRepository.getAllDocumentItems(context)
    }

    fun insertDocumentItem(documentItem: DocumentItem, data: ArrayList<String>) {
        return DocumentDatabaseRepository.insertDocumentItem(documentItem, data, context)
    }

    fun updateDocumentItem(documentItem: DocumentItem, data: ArrayList<String>) {
        return DocumentDatabaseRepository.updateDocumentItem(documentItem, data, context)
    }

    fun deleteDocumentItem(documentItem: DocumentItem) {
        return DocumentDatabaseRepository.deleteDocumentItem(documentItem, context)
    }

    fun deleteAllDocumentItems() {
        return DocumentDatabaseRepository.deleteAllDocumentItems(context)
    }
}