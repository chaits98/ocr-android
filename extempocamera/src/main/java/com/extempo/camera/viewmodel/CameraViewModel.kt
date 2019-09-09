package com.extempo.camera.viewmodel


import android.graphics.Bitmap
import android.graphics.PointF
import android.view.View
import androidx.lifecycle.ViewModel
import com.extempo.camera.utilities.BitmapUtils
import com.extempo.camera.utilities.RectangleDetector
import com.extempo.camera.view.PolygonView

class CameraViewModel: ViewModel() {

    fun getBounds(imageBitmap: Bitmap, polygonView: PolygonView) {
        val rectbounds = RectangleDetector.findRectangle(imageBitmap)
        var pointFs: List<PointF> = emptyList()
        if (rectbounds.isNotEmpty() && rectbounds.size == 4){
            pointFs = listOf(
                PointF(rectbounds[0].x.toFloat(), rectbounds[0].y.toFloat()),
                PointF(rectbounds[1].x.toFloat(), rectbounds[1].y.toFloat()),
                PointF(rectbounds[2].x.toFloat(), rectbounds[2].y.toFloat()),
                PointF(rectbounds[3].x.toFloat(), rectbounds[3].y.toFloat())
            )
        }

        val orderedPoints = BitmapUtils.orderedValidEdgePoints(imageBitmap, pointFs)
        polygonView.setPoints(orderedPoints)
        polygonView.visibility = View.VISIBLE
    }
}