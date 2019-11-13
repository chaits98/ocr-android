package com.extempo.typescan.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.TEXT_SERVICES_MANAGER_SERVICE
import android.graphics.Bitmap
import android.view.textservice.SpellCheckerSession
import android.view.textservice.TextInfo
import android.view.textservice.TextServicesManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.extempo.opticalcharacterrecognizer.model.listeners.InferenceListener
import com.extempo.opticalcharacterrecognizer.utilities.OpticalCharacterDetector
import com.extempo.typescan.model.DocumentItem
import com.extempo.typescan.model.repository.DocumentRepository
import kotlinx.android.synthetic.main.activity_home.*
import java.lang.ref.WeakReference

class TextEditorActivityViewModel(private val documentRepository: DocumentRepository) : ViewModel() {
    var textList: ArrayList<String>? = null
    var documentItem: DocumentItem? = null

    fun runInference(capturedImageBitmap: Bitmap, context: Context, sessionWeakReference: WeakReference<SpellCheckerSession>): LiveData<ArrayList<String>> {
        val liveData = MutableLiveData<ArrayList<String>>()
        println("log_tag: running inference")
        OpticalCharacterDetector.findAlphabets2(capturedImageBitmap,
            object : InferenceListener {
                override fun started() {
                    // show Activity Indicator
                }

                @SuppressLint("RestrictedApi")
                override fun finished(dataList: ArrayList<String>) {
                    for(i in 0 until dataList.size) {
                        sessionWeakReference.get()?.getSentenceSuggestions(Array(1) { TextInfo(dataList[i]) }, 1)
                    }
                    textList = dataList
                    liveData.value = dataList
                }
            }, context)

        return liveData
    }

    fun insertDocumentItem(documentItem: DocumentItem, data: ArrayList<String>) {
        documentRepository.insertDocumentItem(documentItem, data)
    }

    fun updateDocumentItem(documentItem: DocumentItem, data: ArrayList<String>) {
        documentRepository.updateDocumentItem(documentItem, data)
    }
}