package com.extempo.camera.model


import android.hardware.Camera
import android.util.Log


object CameraModel {
    private lateinit var mCamera: Camera
    private var mCameraID: Int = -1

    fun getCameraInstance(): Camera? {
        var cameraId = -1

        val numberOfCamera = Camera.getNumberOfCameras()
        for (i in 0 until numberOfCamera) {
            val mCameraInfo = Camera.CameraInfo()
            Camera.getCameraInfo(i, mCameraInfo)
            if (mCameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i
                break
            } else if (mCameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i
            }
        }
        Log.d("error_tag", "$numberOfCamera")

        var c: Camera? = null
        try {
            c = Camera.open(cameraId)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("error_tag", "camera not opening")
        }

        c?.let { newCamera(it, cameraId) }

        return mCamera
    }

    fun getCameraID(): Int {
        return mCameraID
    }

    private fun newCamera(camera: Camera, id: Int) {
        mCamera = camera
        mCameraID = id
        Log.d("error_tag", "$id")
    }
}