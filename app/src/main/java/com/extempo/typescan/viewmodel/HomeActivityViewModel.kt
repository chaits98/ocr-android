package com.extempo.typescan.viewmodel


import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.extempo.typescan.model.DocumentItem
import com.extempo.typescan.model.repository.DocumentRepository

class HomeActivityViewModel(private val documentRepository: DocumentRepository): ViewModel() {
    var capturedImageUri: Uri? = null
    var capturedImageBitmap: Bitmap? = null

    fun getAlldocumentItems(): LiveData<PagedList<DocumentItem>>? = documentRepository.getAllDocumentItems()
}