package com.extempo.camera.utilities

import com.extempo.camera.viewmodel.CameraViewModelFactory


object InjectorUtils {
    fun provideCameraViewModelFactory(): CameraViewModelFactory {
        return CameraViewModelFactory()
    }
}