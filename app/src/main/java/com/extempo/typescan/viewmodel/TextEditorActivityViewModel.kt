package com.extempo.typescan.viewmodel

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.extempo.opticalcharacterrecognizer.model.listeners.InferenceListener
import com.extempo.opticalcharacterrecognizer.utilities.OpticalCharacterDetector
import com.extempo.typescan.model.DocumentItem
import com.extempo.typescan.model.repository.DocumentRepository
import kotlinx.android.synthetic.main.activity_home.*

class TextEditorActivityViewModel(private val documentRepository: DocumentRepository) : ViewModel() {
    var textList: ArrayList<String>? = null
    var documentItem: DocumentItem? = null

    fun runInference(capturedImageBitmap: Bitmap): LiveData<ArrayList<String>> {
        val liveData = MutableLiveData<ArrayList<String>>()

        OpticalCharacterDetector.findAlphabets2(capturedImageBitmap,
            object : InferenceListener {
                override fun started() {
                    // show Activity Indicator
                }

                @SuppressLint("RestrictedApi")
                override fun finished(dataList: ArrayList<String>) {
                    textList = dataList
                    liveData.value = dataList
                }
            })

        return liveData
    }

    fun insertDocumentItem(documentItem: DocumentItem, data: ArrayList<String>) {
        documentRepository.insertDocumentItem(documentItem, data)
    }

    fun updateDocumentItem(documentItem: DocumentItem, data: ArrayList<String>) {
        documentRepository.updateDocumentItem(documentItem, data)
    }
}