package com.extempo.camera.utilities


import android.content.Context
import android.content.pm.PackageManager

object CameraUtility {
    fun checkCameraHardware(context: Context): Boolean {
        return if (context.packageManager.hasSystemFeature(
                PackageManager.FEATURE_CAMERA
            )
        ) {
            true
        } else context.packageManager.hasSystemFeature(
            PackageManager.FEATURE_CAMERA_FRONT
        )
    }
}