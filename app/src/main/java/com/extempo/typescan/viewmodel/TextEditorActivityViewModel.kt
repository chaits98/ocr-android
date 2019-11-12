package com.extempo.typescan.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.view.textservice.SpellCheckerSession
import android.view.textservice.TextInfo
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.extempo.opticalcharacterrecognizer.model.listeners.InferenceListener
import com.extempo.opticalcharacterrecognizer.utilities.OpticalCharacterDetector
import com.extempo.typescan.model.Author
import com.extempo.typescan.model.DocumentItem
import com.extempo.typescan.model.repository.DocumentRepository
import org.opencv.core.Mat
import java.lang.ref.WeakReference

class TextEditorActivityViewModel(private val documentRepository: DocumentRepository) : ViewModel() {
    var textList: ArrayList<String>? = null
    var documentItem: DocumentItem? = null
    var docCharacterMap: HashMap<String, ArrayList<Mat>>? = null
    var percentageMap: HashMap<Author, Float>? = null

    fun runInference(capturedImageBitmap: Bitmap, context: Context, sessionWeakReference: WeakReference<SpellCheckerSession>): LiveData<ArrayList<String>> {
        val liveData = MutableLiveData<ArrayList<String>>()
        OpticalCharacterDetector.findAlphabets2(capturedImageBitmap,
            object : InferenceListener {
                override fun started() {
                    // show Activity Indicator
                }

                @SuppressLint("RestrictedApi")
                override fun finished(dataList: ArrayList<String>, characterMap: HashMap<String, ArrayList<Mat>>) {
                    for(i in 0 until dataList.size) {
                        sessionWeakReference.get()?.getSentenceSuggestions(Array(1) { TextInfo(dataList[i]) }, 1)
                    }
                    textList = dataList
                    liveData.value = dataList
                    docCharacterMap = characterMap
                }
            }, context)

        return liveData
    }

    fun insertDocumentItem(documentItem: DocumentItem, data: ArrayList<String>) {
        data.forEach { println("log_tag doc item view model $it") }
        documentRepository.insertDocumentItem(documentItem, data)
    }

    fun updateDocumentItem(documentItem: DocumentItem, data: ArrayList<String>) {
        documentRepository.updateDocumentItem(documentItem, data)
    }
}