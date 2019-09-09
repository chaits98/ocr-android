package com.extempo.camera.utilities


import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.PointF
import android.util.Log
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.imgproc.Imgproc
import java.util.*
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt
import android.R.array
import android.R.attr.bitmap
import android.annotation.TargetApi
import android.content.ContentResolver
import android.content.Context
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import android.opengl.ETC1.getHeight
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.extempo.camera.view.PolygonView
import java.io.ByteArrayInputStream
import java.lang.Exception
import java.nio.ByteBuffer


object BitmapUtils {

    fun orderedValidEdgePoints(tempBitmap: Bitmap, pointFs: List<PointF>): Map<Int, PointF> {
        var orderedPoints = PolygonView.PolygonUtils.getOrderedPoints(pointFs)
        if (!PolygonView.PolygonUtils.isValidShape(orderedPoints) || pointFs.isEmpty()) {
            orderedPoints = getOutlinePoints(tempBitmap)
        }
        return orderedPoints
    }

    private fun getOutlinePoints(tempBitmap: Bitmap): Map<Int, PointF> {
        val outlinePoints = HashMap<Int, PointF>()
        outlinePoints[0] = PointF(40f, 40f)
        outlinePoints[1] = PointF(tempBitmap.width.toFloat() - 40, 40f)
        outlinePoints[2] = PointF(tempBitmap.width.toFloat() -40, tempBitmap.height.toFloat() -40)
        outlinePoints[3] = PointF(40f, tempBitmap.height.toFloat() -40)
        return outlinePoints
    }

    fun rotateImage(img: Bitmap?, degree: Int): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        val rotatedImg = img?.let { Bitmap.createBitmap(it, 0, 0, img.width, img.height, matrix, true) }
        img?.recycle()
        return rotatedImg
    }

    private fun flipVertical(img: Bitmap?): Bitmap? {
        val matrix = Matrix()
        matrix.postScale(1F, -1F, (img?.width!!/2).toFloat(), (img.height/2).toFloat())
        val flippedImg = img.let { Bitmap.createBitmap(it, 0, 0, img.width, img.height, matrix, true) }
        img.recycle()
        return flippedImg
    }

    private fun flipHorizontal(img: Bitmap?): Bitmap? {
        val matrix = Matrix()
        matrix.postScale(-1F, 1F, (img?.width!!/2).toFloat(), (img.height/2).toFloat())
        val flippedImg = img.let { Bitmap.createBitmap(it, 0, 0, img.width, img.height, matrix, true) }
        img.recycle()
        return flippedImg
    }

    fun scaleBitmap(bitmap: Bitmap, width: Int): Bitmap {
        val originalWidth = bitmap.width
        val originalHeight = bitmap.height

        val scale = width.toDouble() / originalWidth

        var finalWidth = floor((originalWidth * scale)).toInt()
        var finalHeight = floor((originalHeight * scale)).toInt()
        return Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true)
    }

    fun getCorrectOrientationForUri(imageUri: Uri, context: Context): Bitmap {
        var bitmap: Bitmap
        val imageStream = imageUri.let { context.contentResolver.openInputStream(it) }
        BitmapFactory.decodeStream(imageStream).also { image ->
            bitmap = scaleBitmap(image, 1080)
        }

        try {
            val orientationColumn = arrayOf(MediaStore.Images.ImageColumns.ORIENTATION)
            val cursor = context.contentResolver.query(
                imageUri,
                orientationColumn,
                null,
                null,
                null
            )

            if (cursor?.count != 1) {
                cursor?.close()
                return bitmap
            }
            Log.d("log_tag", "${orientationColumn}")

            cursor.moveToFirst()
            Log.d("log_tag", "count: ${cursor.columnCount}")

            val orientation = cursor.getInt(0)
            Log.d("log_tag", "orientation $orientation")
            cursor.close()

            bitmap = rotateImage(bitmap, orientation)!!
        } catch (e: Exception){
            e.printStackTrace()
            getCorrectOrientationFromExif(imageUri, bitmap)
        }

        return bitmap
    }


    @TargetApi(Build.VERSION_CODES.N)
    fun getCorrectOrientationFromExif(uri: Uri, bitmap: Bitmap): Bitmap {
        try {
            val ei = ExifInterface(uri.path!!)
            return when (ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270)
                ExifInterface.ORIENTATION_FLIP_VERTICAL -> flipVertical(bitmap)
                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> flipHorizontal(bitmap)
                else -> bitmap
            }!!
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bitmap
    }

    fun cropPicture(bitmap: Bitmap, polygonView: PolygonView): Bitmap? {
        val picture = Mat()
        Utils.bitmapToMat(bitmap, picture)
        val pointFMap = polygonView.getPoints()
        if (!PolygonView.PolygonUtils.isValidShape(pointFMap)) {
            return null
        }

        val tl = Point(pointFMap[0]?.x!!.toDouble() + PolygonView.PolygonUtils.adjustment, pointFMap[0]?.y!!.toDouble() + PolygonView.PolygonUtils.adjustment * 2)
        val tr = Point(pointFMap[1]?.x!!.toDouble() + PolygonView.PolygonUtils.adjustment, pointFMap[1]?.y!!.toDouble() + PolygonView.PolygonUtils.adjustment * 2)
        val br = Point(pointFMap[2]?.x!!.toDouble() + PolygonView.PolygonUtils.adjustment, pointFMap[2]?.y!!.toDouble() + PolygonView.PolygonUtils.adjustment * 2)
        val bl = Point(pointFMap[3]?.x!!.toDouble() + PolygonView.PolygonUtils.adjustment, pointFMap[3]?.y!!.toDouble() + PolygonView.PolygonUtils.adjustment * 2)

        Log.d("check-Point", "$tl $tr $br $tl")


        val widthA = sqrt((br.x - bl.x).pow(2.0) + (br.y - bl.y).pow(2.0))
        val widthB = sqrt((tr.x - tl.x).pow(2.0) + (tr.y - tl.y).pow(2.0))

        val dw = max(widthA, widthB)
        val maxWidth = java.lang.Double.valueOf(dw).toInt()


        val heightA = sqrt((tr.x - br.x).pow(2.0) + (tr.y - br.y).pow(2.0))
        val heightB = sqrt((tl.x - bl.x).pow(2.0) + (tl.y - bl.y).pow(2.0))

        val dh = max(heightA, heightB)
        val maxHeight = java.lang.Double.valueOf(dh).toInt()

        val croppedPic = Mat(maxHeight, maxWidth, CvType.CV_8UC4)

        val src_mat = Mat(4, 1, CvType.CV_32FC2)
        val dst_mat = Mat(4, 1, CvType.CV_32FC2)

        src_mat.put(0, 0, tl.x, tl.y, tr.x, tr.y, br.x, br.y, bl.x, bl.y)
        dst_mat.put(0, 0, 0.0, 0.0, dw, 0.0, dw, dh, 0.0, dh)

        val m = Imgproc.getPerspectiveTransform(src_mat, dst_mat)

        Imgproc.warpPerspective(picture, croppedPic, m, croppedPic.size())
        m.release()
        src_mat.release()
        dst_mat.release()

        val bmp: Bitmap
        bmp = Bitmap.createBitmap(
            croppedPic.cols(), croppedPic.rows(),
            Bitmap.Config.ARGB_8888
        )
        Utils.matToBitmap(croppedPic, bmp)

        return bmp
    }
}