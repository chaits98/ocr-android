package com.extempo.camera.view


import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.graphics.*
import android.hardware.Camera
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Surface.*
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.WindowManager
import com.extempo.camera.R
import com.extempo.camera.model.listeners.OnPictureTakenListener
import com.extempo.camera.utilities.BitmapUtils
import com.extempo.camera.utilities.RectangleDetector
import java.io.ByteArrayOutputStream
import java.io.IOException


class CameraPreview(context: Context, private val mCamera: Camera) : SurfaceView(context), SurfaceHolder.Callback, Camera.PreviewCallback {
    private lateinit var paint: Paint
    private var frameData: ByteArray? = null
    private var isProcessing: Boolean = false
    private var points: Map<Int, PointF> = HashMap()
    private val mHolder: SurfaceHolder = holder.apply {
        addCallback(this@CameraPreview)
        setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
    }

    init {
        initPaint()
    }

    private val mHandler = Handler(Looper.getMainLooper())

    override fun surfaceCreated(holder: SurfaceHolder) {
        mCamera.apply {
            try {
                setPreviewDisplay(holder)
                startPreview()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        setWillNotDraw(false)
    }

    override fun onPreviewFrame(data: ByteArray?, camera: Camera?) {
        if (!isProcessing) {
            frameData = data
            mHandler.post {
                isProcessing = true

                val result = convertFrameDataToBitmap()
                if (result != null) {
//                    val scaledBitmap = BitmapUtils.scaleBitmap(result, this.width)
                    val rectbounds = RectangleDetector.findRectangle(result)
                    if (rectbounds.isNotEmpty()) {
                        val pointFs = listOf(
                            PointF(rectbounds[3].x.toFloat(), rectbounds[3].y.toFloat()),
                            PointF(rectbounds[0].x.toFloat(), rectbounds[0].y.toFloat()),
                            PointF(rectbounds[2].x.toFloat(), rectbounds[2].y.toFloat()),
                            PointF(rectbounds[1].x.toFloat(), rectbounds[1].y.toFloat())
                        )
                        points = BitmapUtils.orderedValidEdgePoints(result, pointFs)
                    } else {
                        points = HashMap()
                    }
                    invalidate()
                }

                isProcessing = false
            }
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, w: Int, h: Int) {
        try {
            mCamera.stopPreview()
        } catch (e: Exception) {
            // ignore: tried to stop a non-existent preview
        }

        val parameters = mCamera.parameters
        val display = (context.getSystemService(WINDOW_SERVICE) as WindowManager).defaultDisplay
        val sizes = parameters.getSupportedPreviewSizes()
        val width = sizes[0].width
        val height = sizes[0].height
        when {
            display.rotation == ROTATION_0 -> {
                parameters.setPreviewSize(width, height)
                mCamera.setDisplayOrientation(90)
            }
            display.rotation == ROTATION_90 -> {
                parameters.setPreviewSize(width, height)
            }
            display.rotation == ROTATION_180 -> {
                parameters.setPreviewSize(height, width)
            }
            display.rotation == ROTATION_270 -> {
                parameters.setPreviewSize(width, height)
                mCamera.setDisplayOrientation(180)
            }
        }

        mCamera.parameters = parameters

        if (mHolder.surface == null) {
            return
        }

        val preview = this
        mCamera.apply {
            try {
                setPreviewDisplay(mHolder)
                setPreviewCallback(preview)
                startPreview()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (points.isNotEmpty()) {
            canvas?.drawLine(
                points[0]?.x!!,
                points[0]?.y!!,
                points[1]?.x!!,
                points[1]?.y!!,
                paint
            )
            canvas?.drawLine(
                points[1]?.x!!,
                points[1]?.y!!,
                points[2]?.x!!,
                points[2]?.y!!,
                paint
            )
            canvas?.drawLine(
                points[2]?.x!!,
                points[2]?.y!!,
                points[3]?.x!!,
                points[3]?.y!!,
                paint
            )
            canvas?.drawLine(
                points[0]?.x!!,
                points[0]?.y!!,
                points[3]?.x!!,
                points[3]?.y!!,
                paint
            )
        }
    }

    private fun initPaint() {
        paint = Paint()
        paint.color = resources.getColor(R.color.colorPrimaryDark)
        paint.strokeWidth = 6f
        paint.isAntiAlias = true
    }

    fun takePicture(listener: OnPictureTakenListener?) {
        if (listener == null) {
            return
        }

        val result = convertFrameDataToBitmap()

        if (result != null) {
            listener.onPicReceived(result)
        } else {
            listener.onPicReceived(null)
        }
    }

    private fun convertYuvToJpeg(): ByteArray {
        val cameraParameters = mCamera.parameters
        val width = cameraParameters.previewSize.width
        val height = cameraParameters.previewSize.height
        val yuv = YuvImage(frameData!!, cameraParameters.previewFormat, width, height, null)
        val baos = ByteArrayOutputStream()
        val quality = 100
        yuv.compressToJpeg(Rect(0, 0, width, height), quality, baos)

        return baos.toByteArray()
    }

    private fun convertFrameDataToBitmap(): Bitmap? {
        val imageByteData = convertYuvToJpeg()

        val bitmap = BitmapFactory.decodeByteArray(imageByteData, 0, imageByteData.size)

        return bitmap?.let {
            BitmapUtils.rotateImage(it, 90)
        }
    }
}
