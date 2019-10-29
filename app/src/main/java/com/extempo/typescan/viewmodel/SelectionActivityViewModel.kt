package com.extempo.typescan.viewmodel


import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel

class SelectionActivityViewModel: ViewModel() {
    var capturedImageUri: Uri? = null
    var capturedImageBitmap: Bitmap? = null
}