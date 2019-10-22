package com.extempo.opticalcharacterrecognizer.utilities

import android.app.Activity
import android.graphics.Bitmap
import android.util.Log
import com.extempo.opticalcharacterrecognizer.model.Result
import com.extempo.opticalcharacterrecognizer.model.listeners.InferenceListener
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel.MapMode.READ_ONLY
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.core.Core
import java.io.BufferedReader
import java.io.InputStreamReader


object OpticalCharacterDetector {
    private var modelFile = "model.tflite"
    private var tflite: Interpreter? = null
    private var labelList: List<String>? = null

    fun loadModel(activity: Activity) {
        try {
            tflite = Interpreter(loadModelFile(activity, modelFile))
            labelList = loadLabelList(activity)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Throws(Exception::class)
//    fun findAlphabets(bitmap: Bitmap, inferenceListener: InferenceListener) {
    fun findAlphabets(bitmap: Bitmap, inferenceListener: InferenceListener) {
        var alphabetList: List<Char> = emptyList()

        inferenceListener.started(bitmap)

        var tempMat = Mat()
        var source = Mat()
        Utils.bitmapToMat(bitmap, tempMat)
        Imgproc.cvtColor(tempMat, source, Imgproc.COLOR_BGR2GRAY)
        var result = Mat()
        Imgproc.adaptiveThreshold(
            source,
            result,
            255.0,
            Imgproc.ADAPTIVE_THRESH_MEAN_C,
            Imgproc.THRESH_BINARY_INV,
            15,
            15.0
        )

        var contours = ArrayList<MatOfPoint>()
//        Imgproc.Canny(result, result, 120.0, 200.0)
        Imgproc.findContours(result, contours, Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE)
        contours = filterContours(contours)
        val sentences  = formSentences(contours)
        println("log_tag: " + contours.size + "  " + sentences[0].size)

        for (sentence in sentences) {
            for (cnt in sentence) {
                val rect = Imgproc.boundingRect(cnt)
                val cropped = Mat(result, rect)
                val resizeImage = Mat()
                val sz = Size(100.0, 100.0)
                val sz2 = Size(28.0, 28.0)
//                if (cropped.width() >= 1.2 * cropped.height()) {
//
//                } else {
                    Imgproc.resize(cropped, resizeImage, sz)
//                }
                Log.d("log_tag", "${cropped.width()} ${cropped.height()} ${cropped[0, 0].size}")
                result = imagePadding(resizeImage, 128)
                Imgproc.resize(result, result, sz2)
                Imgproc.dilate(result, result, Mat(), Point(-1.0, -1.0))
                println("log_tag: " + result.dump())
                val  result2 = findCharacter(result)
                println("log_tag character found: ${result2.getCharacter()} with confidence: ${result2.getConfidence()*100}%")
                break
            }
            break
        }



//        for (cnt in contours) {
//            val rect = Imgproc.boundingRect(cnt)
//            Imgproc.rectangle(result, rect.tl(), rect.br(), Scalar(255.0, 110.0, 255.0))
////            Imgproc.drawContours(result, contours, contours.indexOf(cnt), Scalar(100.0, 0.0, 0.0, 0.8), 2)
//        }

        val bmp = Bitmap.createBitmap(
            result.cols(), result.rows(),
            Bitmap.Config.ARGB_8888
        )
        Utils.matToBitmap(result, bmp)
        inferenceListener.finished(bmp)
//        return alphabetList
    }

    private fun imagePadding(source: Mat, blockSize: Int): Mat {
        val width = source.width()
        val height = source.height()
        var bottomPadding = 0
        var rightPadding = 0
        var topPadding = 0
        var leftPadding = 0
        if (width % blockSize != 0) {
            bottomPadding = (blockSize - width % blockSize) / 2
            topPadding = bottomPadding
        }
        if (height % blockSize != 0) {
            rightPadding = (blockSize - height % blockSize) / 2
            leftPadding = rightPadding
        }
        println("log_tag $topPadding, $bottomPadding, $leftPadding, $rightPadding")
        Core.copyMakeBorder(
            source,
            source,
            topPadding,
            bottomPadding,
            leftPadding,
            rightPadding,
            Core.BORDER_CONSTANT,
            Scalar.all(0.0)
        )
        return source
    }

    private fun filterContours(contours: ArrayList<MatOfPoint>): ArrayList<MatOfPoint> {
        val newContours = ArrayList<MatOfPoint>()
        for (cnt in contours) {
            if(Imgproc.contourArea(cnt) > 20) {
                newContours.add(cnt)
            }
        }
        return newContours
    }

    private fun findCharacter(mat: Mat): Result {
        val transposedMat = Mat()
        Core.transpose(mat, transposedMat)
        var result = -1
        val data = reshapeData(transposedMat)
        var output = Array(1) { FloatArray(47) }
        tflite?.run(data, output)
        var max = 0.0f
        for (i in 0..46) {
            if (output[0][i] > max) {
                max = output[0][i]
                result = i
            }
            println("log_tag: output ${labelList?.get(i)} ${output[0][i]}")
        }
        return if (result > -1) {
            Result(output[0][result], labelList?.get(result)!!)
        } else {
            Result(0.0f, "")
        }
    }

    private fun reshapeData(mat: Mat): Array<Array<Array<FloatArray>>> {
        val data = Array(1) { Array(28) { Array(28) { FloatArray(1) } } }
        for (i in 0..27) {
            for (j in 0..27) {
                data[0][i][j][0] = mat[i, j][0].toFloat()
            }
        }
        return data
    }

//    private fun reshapeData(mat: Mat): Array<Array<Array<Float>>> {
//        var data = Array(28) { Array(28) { arrayOf(0.0f) } }
//        for (i in 0..27) {
//            for (j in 0..27) {
//                data[i][j] = arrayOf(mat[i, j][0].toFloat())
//            }
//        }
//        return data
//    }

//    private fun reshapeData(mat: Mat): Array<Array<Int>> {
//        var data = Array(28) { Array(28) { 0 } }
//        for (i in 0..27) {
//            for (j in 0..27) {
//                data[i][j] = mat[i, j][0].toInt()
//            }
//        }
//        return data
//    }

    @Throws(IOException::class)
    private fun loadLabelList(activity: Activity): List<String> {
        val labels = ArrayList<String>()
        val reader = BufferedReader(InputStreamReader(activity.assets.open(getLabelPath())))
        var line: String
        val iterator = reader.lineSequence().iterator()
        while (iterator.hasNext()) {
            line = iterator.next()
            labels.add(line)
        }
        reader.close()
        return labels
    }

    private fun getLabelPath(): String {
        return "mapping.txt"
    }

    private fun formWords(contours: ArrayList<MatOfPoint>): ArrayList<Rect> {
        val words = ArrayList<Rect>()
        val sortedContours = sortLeftToRight(contours)

        var index = 0
        while(true) {
            val c = sortedContours[index]
            val r = Imgproc.boundingRect(c)
            var top = r.tl().y
            var bottom = r.br().y
            var left = r.tl().x
            var right = r.br().x
            while (true) {
                if(index >= sortedContours.size - 2){
                    break
                }
                val cnt = sortedContours[index]
                val rect1 = Imgproc.boundingRect(cnt)
                val cnt1 = sortedContours[index+1]
                val rect2 = Imgproc.boundingRect(cnt1)
                if (rect2.tl().x <= rect1.br().x + rect1.height) {
                    if (rect2.tl().y < top){
                        top = rect2.tl().y
                    }
                    if (rect2.br().y > bottom) {
                        bottom = rect2.br().y
                    }
                    right = rect2.br().x
                    index++
                } else {
                    index++
                    break
                }
            }
            words.add(Rect(left.toInt(), top.toInt() , (right - left).toInt(), (bottom - top).toInt()))
            if(index >= sortedContours.size - 2){
                break
            }
        }

        return words
    }

    private fun formSentences(contours: ArrayList<MatOfPoint>): ArrayList<ArrayList<MatOfPoint>> {
        val sentences = ArrayList<ArrayList<MatOfPoint>>()
        val sortedContours = sortTopToBottom(contours)
        var index = 0
        while(true) {
            if (sortedContours.size == 0) break
            val cnt = sortedContours[index]
            val sentence = ArrayList<MatOfPoint>()
            val rect1 = Imgproc.boundingRect(cnt)
            while (true) {
                if(index == sortedContours.size && index != 0){
                    break
                }
                val cnt1 = sortedContours[index]
                val rect2 = Imgproc.boundingRect(cnt1)
                if ((rect2.tl().y <= rect1.br().y && rect2.tl().y >= rect1.tl().y) || (rect2.br().y >= rect1.tl().y && rect2.br().y <= rect1.br().y)) {
                    sentence.add(cnt1)
                    index++
                } else {
                    break
                }
            }
            sentences.add(sortLeftToRight(sentence))
            if(index == sortedContours.size && index != 0){
                break
            }
        }

        return sentences
    }

    private fun sortTopToBottom(contours: ArrayList<MatOfPoint>): ArrayList<MatOfPoint> {
        contours.sortWith(Comparator { o1, o2 ->
            val rect1 = Imgproc.boundingRect(o1)
            val rect2 = Imgproc.boundingRect(o2)
            rect1.tl().y.compareTo(rect2.tl().y)
        })
        return contours
    }

    private fun sortLeftToRight(contours: ArrayList<MatOfPoint>): ArrayList<MatOfPoint> {
        contours.sortWith(Comparator { o1, o2 ->
            val rect1 = Imgproc.boundingRect(o1)
            val rect2 = Imgproc.boundingRect(o2)
            rect1.tl().x.compareTo(rect2.tl().x)
        })
        return contours
    }

    @Throws(IOException::class)
    private fun loadModelFile(activity: Activity, MODEL_FILE: String): MappedByteBuffer {
        val fileDescriptor = activity.assets.openFd(MODEL_FILE)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(READ_ONLY, startOffset, declaredLength)
    }
}