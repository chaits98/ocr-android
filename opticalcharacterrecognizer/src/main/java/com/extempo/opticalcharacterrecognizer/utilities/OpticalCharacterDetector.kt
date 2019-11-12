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
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel.MapMode.READ_ONLY
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.core.Core
import java.io.*
import android.content.Context
import com.extempo.typescan.utilities.CharacterMapFactory
import org.opencv.imgcodecs.Imgcodecs
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


object OpticalCharacterDetector {
    private var tflite: Interpreter? = null
    private var labelList: List<String>? = null
    private const val IM_DIMEN = 128
    private var modelFile = "merged$IM_DIMEN.tflite"
    private var characterMap: HashMap<String, ArrayList<Mat>> = CharacterMapFactory.initCharMap()

    fun loadModel(activity: Activity) {
        try {
            tflite = Interpreter(loadModelFile(activity, modelFile))
            labelList = loadLabelList(activity)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Throws(Exception::class)
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
                val sz2 = Size(IM_DIMEN.toDouble(), IM_DIMEN.toDouble())
                Imgproc.resize(cropped, resizeImage, sz)
                Log.d("log_tag", "${cropped.width()} ${cropped.height()} ${cropped[0, 0].size}")
                resizeImage = imagePadding(resizeImage, 128)
                Imgproc.resize(resizeImage, resizeImage, sz2)
                Imgproc.dilate(resizeImage, resizeImage, Mat(), Point(-1.0, -1.0))
                print(resizeImage.dump())
                val  result2 = findCharacter(resizeImage)
                s += result2.getCharacter()
                println("log_tag character found: ${result2.getCharacter()} with confidence: ${result2.getConfidence()*100}%")
            }
            dataList.add(s)
        }
        dataList.forEach { println("dataList: $it") }
//        inferenceListener.finished(dataList)
    }

    @Throws(Exception::class)
    fun findAlphabets2 (bitmap: Bitmap, inferenceListener: InferenceListener, context: Context) {
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
            101,
            40.0
        )

        val sentences  = formSentences(result)

        for (sentence in sentences) {
            var s = ""
            val words = formLetters(sentence)
            for (word in words) {
                for (letter in word) {
                    var resizeImage: Mat
                    var contours = ArrayList<MatOfPoint>()
                    Imgproc.findContours(letter, contours, Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)
                    val rectCrop = Imgproc.boundingRect(contours[0])
                    val cropped = Mat(letter, rectCrop)
                    val sz2 = Size(IM_DIMEN.toDouble(), IM_DIMEN.toDouble())
                    val width = cropped.width()
                    val height = cropped.height()
                    resizeImage = if (height > width) {
                        resizeImage(cropped, newHeight = 1000.0, newWidth = null)

                    } else {
                        resizeImage(cropped, newHeight = null, newWidth = 1000.0)
                    }
                    Imgproc.dilate(resizeImage, resizeImage, Mat(), Point(-1.0, -1.0))
                    resizeImage = imagePadding(resizeImage, 2000)
                    Imgproc.resize(resizeImage, resizeImage, sz2)

                    val  result2 = findCharacter(resizeImage)

                    //Add mat to corresponding array of detected character
                    this.characterMap[result2.getCharacter()]?.add(resizeImage)

                    //Lowercase letters for easier spellchecking
                    s += result2.getCharacter().toLowerCase(Locale.getDefault())
                    println("log_tag character found: ${result2.getCharacter()} with confidence: ${result2.getConfidence()*100}%")

                    //Save mat with inference information to a file
                    val file = File(context.filesDir, result2.getCharacter() + result2.getConfidence() + ".png")
                    Imgcodecs.imwrite(file.toString(), resizeImage)
                }
                s += " "
            }
            dataList.add(s)
        }
        inferenceListener.finished(dataList, this.characterMap)
    }

    private fun resizeImage (mat: Mat, newHeight: Double?, newWidth: Double?): Mat {
        val result = Mat()
        val size: Size
        val width = mat.width()
        val height = mat.height()
        val ratio: Double

        when {
            newWidth == null -> {
                ratio = newHeight!! / height.toDouble()
                size = Size((width * ratio), newHeight)
            }
            newHeight == null -> {
                ratio = newWidth / width.toDouble()
                size = Size(newWidth, (height * ratio))
            }
            else -> return mat
        }

        Imgproc.resize(mat, result, size)

        return result
    }

    private fun formSentences(mat: Mat): ArrayList<Mat> {
        val sentences = ArrayList<Mat>()
        val segmentIndices = ArrayList<Int>()
        val sumRange = FloatArray(mat.height()) { 0.0f }
        val segmentList = BooleanArray(mat.height()) { false }
        var segmentPositionEnd = -1
        var segmentPositionStart = -1

        for(i in 0 until mat.height()) {
            for(j in 0 until mat.width()) {
                sumRange[i] += mat[i, j][0].toFloat()
            }
        }

        val minVal = sumRange.min()!!

        for (k in 0 until sumRange.size) {
            if (sumRange[k] <= minVal) {
                segmentList[k] = true
            }
        }

        var initial = true

        for (k in 0 until segmentList.size) {
            if (segmentList[k]) {
                if (segmentPositionStart == -1 && !initial) {
                    segmentPositionStart = k
                }
                if (initial) initial = false
                if (segmentPositionEnd != -1) {
                    segmentIndices.add(k)
                    segmentPositionEnd = -1
                }
            } else {
                if (segmentPositionEnd == -1) {
                    segmentPositionEnd = k - 1
                }

                if (segmentPositionStart != -1) {
                    segmentIndices.add(k)
                    segmentPositionStart = -1
                }
            }
        }

        segmentIndices.forEach { println("log_tag segmentIndices: $it") }
        segmentIndices.forEach { println("log_tag: segmentIndices: $it") }

        try {
            for (x in 0 until segmentIndices.size step 2) {
                val temp = mat.clone()
                val rect = Rect(0, segmentIndices[x], mat.width(), segmentIndices[x+1] - segmentIndices[x])
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
        val segmentColumnIndices = ArrayList<Int>()
        var segmentPositionEnd = -1
        var segmentPositionStart = -1
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
        var initial = true
        for (k in 0 until segmentList.size) {
            if (segmentList[k]) {
                if (segmentPositionStart == -1 && !initial) {
                    segmentPositionStart = k
                }
                if (initial) initial = false
                if (segmentPositionEnd != -1) {
                    segmentColumnIndices.add(k)
                    segmentPositionEnd = -1
                }
            } else {
                if (segmentPositionEnd == -1) {
                    segmentPositionEnd = k - 1
                }

                if (segmentPositionStart != -1) {
                    segmentColumnIndices.add(k)
                    segmentPositionStart = -1
                }
            }
        }
        var wordThreshold = 0.45 * mat.height()
        var tempLetterList = ArrayList<Mat>()
        println("log_tag: segment column indices size: " + segmentColumnIndices.size)
        for (i in 0 until segmentColumnIndices.size step 2) {
            var temp = 0
            if (i <= segmentColumnIndices.size - 2) temp = segmentColumnIndices[i+2] - segmentColumnIndices[i+1]
            val rect = Rect(segmentColumnIndices[i], 0, segmentColumnIndices[i+1] - segmentColumnIndices[i], mat.height())
            val cropped = Mat(mat, rect)
            tempLetterList.add(cropped)
            if (temp >= wordThreshold) {
                words.add(tempLetterList)
                tempLetterList = ArrayList()
            }
        }
        words.add(tempLetterList)
        return words
    }

    private fun imagePadding(source: Mat, blockSize: Int): Mat {
        val width = source.width()
        val height = source.height()
        var bottomPadding = 0
        var rightPadding = 0
        var topPadding = 0
        var leftPadding = 0

        rightPadding = (blockSize - width) / 2
        leftPadding = blockSize - (rightPadding + width)
        topPadding = (blockSize - height) / 2
        bottomPadding = blockSize - (topPadding + height)

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
        var result = -1
        val data = reshapeData(mat)
        val output = Array(1) { FloatArray(48) }
        tflite?.run(data, output)
        var max = 0.0f
        for (i in 0..47) {
            if (output[0][i] > max) {
                max = output[0][i]
                result = i
            }
        }

        return if (result > -1) {
            println("log_tag: $result")
            Result(output[0][result], labelList?.get(result-1)!!)
        } else {
            Result(0.0f, "")
        }
    }

    private fun reshapeData(mat: Mat): Array<Array<Array<FloatArray>>> {
        val data = Array(1) { Array(IM_DIMEN) { Array(IM_DIMEN) { FloatArray(1) } } }
        for (i in 0 until IM_DIMEN) {
            for (j in 0 until IM_DIMEN) {
                data[0][i][j][0] = if(mat[i, j][0].toFloat() > 0) 255.0f else 0.0f
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
        for (i in 0 until labels.size) {
            println("log_tag: $i ${labels[i]}")
        }
        return labels
    }

    private fun getLabelPath(): String {
        return "mapping.txt"
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


    private val mapper = hashMapOf(
        "0" to arrayListOf("O", "D"),
        "1" to arrayListOf("L", "I", "J", "7"),
        "2" to arrayListOf("Z"),
        "3" to emptyList<String>(),
        "4" to arrayListOf("Y"),
        "5" to emptyList<String>(),
        "6" to emptyList<String>(),
        "7" to arrayListOf("I", "L", "1"),
        "8" to emptyList<String>(),
        "9" to emptyList<String>(),
        "a" to arrayListOf("0", "D", "O"),
        "b" to arrayListOf("f"),
        "d" to emptyList<String>(),
        "e" to emptyList<String>(),
        "f" to arrayListOf("b"),
        "g" to emptyList<String>(),
        "h" to arrayListOf("n"),
        "n" to arrayListOf("h", "r"),
        "q" to emptyList<String>(),
        "r" to arrayListOf("M", "H"),
        "t" to arrayListOf("E"),
        "A" to emptyList<String>(),
        "B" to emptyList<String>(),
        "C" to emptyList<String>(),
        "D" to arrayListOf("0", "O"),
        "E" to arrayListOf("t"),
        "F" to emptyList<String>(),
        "G" to emptyList<String>(),
        "H" to arrayListOf("M"),
        "I" to arrayListOf("L", "1", "J", "7"),
        "J" to arrayListOf("I", "Y"),
        "K" to emptyList<String>(),
        "L" to emptyList<String>(),
        "M" to arrayListOf("H"),
        "N" to emptyList<String>(),
        "O" to arrayListOf("D", "0"),
        "P" to emptyList<String>(),
        "Q" to emptyList<String>(),
        "R" to emptyList<String>(),
        "S" to emptyList<String>(),
        "T" to emptyList<String>(),
        "U" to emptyList<String>(),
        "V" to emptyList<String>(),
        "W" to emptyList<String>(),
        "X" to emptyList<String>(),
        "Y" to arrayListOf("J"),
        "Z" to arrayListOf("2")
    )

    fun getInterchangableCharacterList(str: String): ArrayList<String> {
        var output = ArrayList<String>()
        output = mapper[str] as ArrayList<String>
        return output
    }
}