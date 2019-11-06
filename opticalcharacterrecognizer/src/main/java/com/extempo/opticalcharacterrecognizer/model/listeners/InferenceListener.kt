package com.extempo.opticalcharacterrecognizer.model.listeners

import android.graphics.Bitmap

interface InferenceListener {
    fun started() //bitmap is the original source bitmap on which model is run
//    fun finished(bitmap: Bitmap) //bitmap is the final bitmap with contours drawn, to be changed to list of recognised characters
    fun finished(dataList: ArrayList<String>) //will implement this later
}