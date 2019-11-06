package com.extempo.typescan.viewmodel

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.View
import androidx.lifecycle.ViewModel
import com.extempo.opticalcharacterrecognizer.model.listeners.InferenceListener
import com.extempo.opticalcharacterrecognizer.utilities.OpticalCharacterDetector
import com.extempo.typescan.model.repository.DocumentRepository
import kotlinx.android.synthetic.main.activity_home.*

class TextActivityViewModel(private val documentRepository: DocumentRepository) : ViewModel() {
    var textList: ArrayList<String> = ArrayList()

    fun runInference(capturedImageBitmap: Bitmap) {
        OpticalCharacterDetector.findAlphabets(capturedImageBitmap,
            object : InferenceListener {
                override fun started(bitmap: Bitmap) {
                    // show Activity Indicator
                }

                @SuppressLint("RestrictedApi")
                override fun finished(bitmap: Bitmap) {

                }
            })
    }
}