package com.extempo.camera.model.listeners


import android.graphics.Bitmap

interface OnPictureTakenListener {
    fun onPicReceived(bitmap: Bitmap?)
}