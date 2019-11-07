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
import java.util.concurrent.ThreadPoolExecutor


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
        var dataList: ArrayList<String> = ArrayList()

        inferenceListener.started()

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
        Imgproc.findContours(result, contours, Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE)
        println("log_tag: " + result.height() + " " + result.width())
        contours = filterContours(contours)
        val sentences  = formSentences(contours)

        for (sentence in sentences) {
            var s = ""
            var x = 0.0
            for (cnt in sentence) {
                val rect = Imgproc.boundingRect(cnt)
                if (rect.x - x > rect.width * 0.7) {
                    s += " "
                }
                x = rect.br().x
                val cropped = Mat(result, rect)
                var resizeImage = Mat()
                val sz = Size(100.0, 100.0)
                val sz2 = Size(28.0, 28.0)
                Imgproc.resize(cropped, resizeImage, sz)
                Log.d("log_tag", "${cropped.width()} ${cropped.height()} ${cropped[0, 0].size}")
                resizeImage = imagePadding(resizeImage, 128)
                Imgproc.resize(resizeImage, resizeImage, sz2)
//                Imgproc.Canny(cropped, cropped, 120.0, 200.0)
//                val element = Imgproc.getStructuringElement(
//                Imgproc.MORPH_RECT,
//                    Size(1.0, 1.0)
//                )
//                Imgproc.erode(cropped, cropped, element)
//                var segmentedContours = segmentContour(cropped)
//                println("log_tag: " + result.dump())
                Imgproc.dilate(resizeImage, resizeImage, Mat(), Point(-1.0, -1.0))
                print(resizeImage.dump())
                val  result2 = findCharacter(resizeImage)
                s += result2.getCharacter()
                println("log_tag character found: ${result2.getCharacter()} with confidence: ${result2.getConfidence()*100}%")
            }
            dataList.add(s)
        }



//        for (cnt in contours) {
//            val rect = Imgproc.boundingRect(cnt)
//            Imgproc.rectangle(result, rect.tl(), rect.br(), Scalar(255.0, 110.0, 255.0))
////            Imgproc.drawContours(result, contours, contours.indexOf(cnt), Scalar(100.0, 0.0, 0.0, 0.8), 2)
//        }

//        val bmp = Bitmap.createBitmap(
//            result.cols(), result.rows(),
//            Bitmap.Config.ARGB_8888
//        )
//        Utils.matToBitmap(result, bmp)
        dataList.forEach { println("dataList: $it") }
        inferenceListener.finished(dataList)
    }

    @Throws(Exception::class)
    fun findAlphabets2 (bitmap: Bitmap, inferenceListener: InferenceListener) {
        var dataList: ArrayList<String> = ArrayList()

        inferenceListener.started()

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

        val element = Imgproc.getStructuringElement(
        Imgproc.MORPH_RECT,
            Size(3.0, 3.0)
        )
        Imgproc.erode(result, result, element)
        Imgproc.dilate(result, result, Mat(), Point(-1.0, -1.0))

        val sentences  = formSentences(result)

        for (sentence in sentences) {
            var s = ""
            val words = formLetters(sentence)
            for (word in words) {
                for (letter in word) {
                    var resizeImage = Mat()
                    val sz = Size(100.0, 100.0)
                    val sz2 = Size(28.0, 28.0)
                    Imgproc.resize(letter, resizeImage, sz)
//                    resizeImage = imagePadding(resizeImage, 128)
                    Imgproc.resize(resizeImage, resizeImage, sz2)
//                    Imgproc.dilate(resizeImage, resizeImage, Mat(), Point(-1.0, -1.0))
                    println(resizeImage.dump())
                    println(" a")
                    println(" ")
                    println(" a")
                    println(" ")
                    println(" a")
                    println(" ")
                    println(" a")
                    println(" ")
                    println(" a")
                    println(" ")
                    println(" a")
                    println(" ")
                    val  result2 = findCharacter(resizeImage)
                    s += result2.getCharacter()
                    println("log_tag character found: ${result2.getCharacter()} with confidence: ${result2.getConfidence()*100}%")
                }
                s += " "
            }
            dataList.add(s)
        }
        inferenceListener.finished(dataList)
    }

    private fun formSentences(mat: Mat): ArrayList<Mat> {
        val sentences = ArrayList<Mat>()
        val segmentIndices = ArrayList<Int>()
        val sumRange = FloatArray(mat.height()) { 0.0f }
        val segmentList = BooleanArray(mat.height()) { false }
        var segmentCounter = 0
        var segmentPositionStart = -1

        for(i in 0 until mat.height()) {
            for(j in 0 until mat.width()) {
                sumRange[i] += mat[i, j][0].toFloat()
            }
//            sumRange[i] += 255.0f
        }

        val minVal = sumRange.min()!!

        for (k in 0 until sumRange.size) {
            if (sumRange[k] <= minVal) {
                segmentList[k] = true
            }
        }

        for (k in 0 until segmentList.size) {
            if (segmentList[k]) {
                segmentCounter++
                if (segmentPositionStart == -1) {
                    segmentPositionStart = k
                }
            } else {
                if (segmentPositionStart != -1) {
                    segmentIndices.add((segmentPositionStart + (k - segmentPositionStart) / 2))
                    segmentPositionStart = -1
                }
            }
        }

        segmentIndices.add((segmentPositionStart + (mat.height() - segmentPositionStart) / 2))

        try {
            var segmentRectangleStart = segmentIndices[0]
            for (x in 1 until segmentIndices.size) {
                val temp = mat.clone()
                val rect = Rect(0, segmentRectangleStart, mat.width(), segmentIndices[x] - segmentRectangleStart)
                segmentRectangleStart = segmentIndices[x]
                val cropped = Mat(temp, rect)
                sentences.add(cropped)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return sentences
    }

    private fun formLetters(mat: Mat): ArrayList<ArrayList<Mat>> {
        val words = ArrayList<ArrayList<Mat>>()
//        println(mat.dump())
//        println(" a")
//        println(" ")
//        println(" a")
//        println(" ")
//        println(" a")
//        println(" ")
//        println(" a")
//        println(" ")
//        println(" a")
//        println(" ")
//        println(" a")
//        println(" ")
        val segmentColumnIndices = ArrayList<Int>()
        var segmentCounter = 0
        var segmentPositionStart = -1
        val segmentList = BooleanArray(mat.width()) { false }
        val sumRange = FloatArray(mat.width()) { 0.0f }

        for(i in 0 until mat.width()) {
            for(j in 0 until mat.height()) {
                sumRange[i] += mat[j, i][0].toFloat()
            }
        }

        val minVal = sumRange.min()!!

        println("log_tag: $minVal")

        for (k in 0 until sumRange.size) {
            if (sumRange[k] <= minVal*2) {
                segmentList[k] = true
            }
        }

        for (k in 0 until segmentList.size) {
            if (segmentList[k]) {
                segmentCounter++
                if (segmentPositionStart == -1) {
                    println("counter start")
                    segmentPositionStart = k
                }
            } else {
                if (segmentPositionStart != -1) {
                    println("adding")
                    segmentColumnIndices.add((segmentPositionStart + (k - segmentPositionStart) / 2))
                    segmentPositionStart = -1
                }
            }
        }
        segmentColumnIndices.add((segmentPositionStart + (mat.width() - segmentPositionStart) / 2))

        segmentColumnIndices.forEach { println("log_tag segmentIndices: $it") }

        var minValDiff = 999

        for (i in 1 until segmentColumnIndices.size) {
            val temp = segmentColumnIndices[i] - segmentColumnIndices[i-1]
            if (temp < minValDiff) {
                minValDiff = temp
            }
        }

        var tempLetterList = ArrayList<Mat>()
        var segmentRectangleStart = segmentColumnIndices[0]

        for (i in 1 until segmentColumnIndices.size) {
            val temp = segmentColumnIndices[i] - segmentColumnIndices[i-1]
            if (temp >= minValDiff * 1.2) {
                words.add(tempLetterList)
                tempLetterList = ArrayList()
            }
            println("log_tag $segmentRectangleStart, 0, ${mat.width()}, ${segmentColumnIndices[i] - segmentRectangleStart}")
            val rect = Rect(segmentRectangleStart, 0, segmentColumnIndices[i] - segmentRectangleStart, mat.height())
            segmentRectangleStart = segmentColumnIndices[i]
            val cropped = Mat(mat, rect)
            tempLetterList.add(cropped)
        }
        words.add(tempLetterList)

        return words
    }

    private fun segmentContour(mat: Mat): ArrayList<Mat> {
        val result = ArrayList<Mat>()
        val segmentColumnIndices = ArrayList<Int>()
        val threshold = (mat.width() * 0.10).toInt()
        var nonSegmentCounter = 0
        var segmentPositionStart = -1
        var segmentPositionEnd = -1
        val segmentList = BooleanArray(mat.width()) { false }
        val sumRange = FloatArray(mat.width()) { 0.0f }
        for(i in 0 until mat.width()) {
            for(j in 0 until mat.height()) {
                sumRange[i] += mat[j, i][0].toFloat()
            }
        }

        val minVal = sumRange.min()!!

        for (k in 0 until sumRange.size) {
            if (sumRange[k] <= minVal*2) {
                segmentList[k] = true
            }
        }

        for (k in 0 until segmentList.size) {
            if (!segmentList[k]) {
                nonSegmentCounter++
                if (segmentPositionStart == -1) {
                    segmentPositionStart = k
                }
                if (segmentPositionEnd == -1) {
                    segmentPositionEnd = k
                } else {
                     if (nonSegmentCounter > threshold) {
                         val segment = (segmentPositionStart + ((segmentPositionEnd - segmentPositionStart) / 2))
                         println("Segment $segment nonSegmentCounter $nonSegmentCounter k $k")
                         segmentColumnIndices.add(segment)
                         nonSegmentCounter = 0
                         segmentPositionStart = -1
                         segmentPositionEnd = -1
                     }
                }
            } else {
                if (segmentPositionStart == -1) {
                    segmentPositionStart = k
                } else if (nonSegmentCounter > threshold) {
                    nonSegmentCounter = 0
                    segmentPositionEnd = -1
                }
            }
        }

        for (column in segmentColumnIndices) {
            for(j in 0 until mat.height()) {
                mat[j, column][0] = 255.0
            }
        }
        print(mat.dump())

        return result
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