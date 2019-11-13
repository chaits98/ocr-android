package com.extempo.opticalcharacterrecognizer.model.listeners

import android.graphics.Bitmap
import com.extempo.opticalcharacterrecognizer.model.CharImage

interface InferenceListener {
    fun started() //bitmap is the original source bitmap on which model is run
//    fun finished(bitmap: Bitmap) //bitmap is the final bitmap with contours drawn, to be changed to list of recognised characters
    fun finished(dataList: ArrayList<String>, charImageList: ArrayList<CharImage>) //will implement this later
}