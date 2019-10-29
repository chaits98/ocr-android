package com.extempo.opticalcharacterrecognizer.model.listeners

import android.graphics.Bitmap

interface InferenceListener {
    fun started(bitmap: Bitmap) //bitmap is the original source bitmap on which model is run
    fun finished(bitmap: Bitmap) //bitmap is the final bitmap with contours drawn, to be changed to list of recognised characters
//    fun finished(List<Char>) //will implement this later
}