package com.extempo.typescan.viewmodel

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Context.TEXT_SERVICES_MANAGER_SERVICE
import android.graphics.Bitmap
import android.view.textservice.SpellCheckerSession
import android.view.textservice.TextInfo
import android.view.textservice.TextServicesManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.extempo.opticalcharacterrecognizer.model.CharImage
import com.extempo.opticalcharacterrecognizer.model.listeners.InferenceListener
import com.extempo.opticalcharacterrecognizer.utilities.OpticalCharacterDetector
import com.extempo.typescan.model.Author
import com.extempo.typescan.model.DocumentItem
import com.extempo.typescan.model.repository.AuthorRepository
import com.extempo.typescan.model.repository.DocumentRepository
import com.extempo.typescan.utilities.WordSuggesterUtil
import kotlinx.android.synthetic.main.activity_home.*
import org.opencv.core.Mat
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import android.app.ProgressDialog
import androidx.annotation.WorkerThread


class TextEditorActivityViewModel(private val documentRepository: DocumentRepository, private val authorRepository: AuthorRepository) : ViewModel() {
    var textList: ArrayList<String>? = null
    var documentItem: DocumentItem? = null
    var author: Author? = null
    var charImageArray: ArrayList<CharImage>? = null
    var progressDialog: ProgressDialog? = null

    init {

    }

    fun runInference(capturedImageBitmap: Bitmap, activity: Activity, sessionWeakReference: WeakReference<SpellCheckerSession>): LiveData<ArrayList<String>> {
        val liveData = MutableLiveData<ArrayList<String>>()
        OpticalCharacterDetector.findAlphabets2(capturedImageBitmap,
            object : InferenceListener {
                override fun started() {
                    println("inference called: started")
                    activity.runOnUiThread {
                        progressDialog = ProgressDialog(activity)
                        progressDialog?.setMessage("Please wait...")
                        progressDialog?.setCancelable(false)
                        progressDialog?.show()
                    }
                }

                @SuppressLint("RestrictedApi")
                override fun finished(dataList: ArrayList<String>, matArray: ArrayList<CharImage>) {
//                    for (sentence in dataList) {
//                        sentence.forEach { char ->
//                            if(char != ' ' || char != '\u0000') {
//
//                            }
//                        }
//                    }
//                    for(i in 0 until dataList.size) {
//                        for (j in 0 until dataList[i].length) {
//                            val words = StringTokenizer(dataList[i])
//                            while(words.hasMoreTokens()) {
//                                val word = words.nextToken()
//                                val interchangeables = ArrayList<ArrayList<String>>()
//                                word.forEach {
//                                    interchangeables.add(
//                                        CharImage.getInterchangeableCharacterList(
//                                            it.toString()
//                                        )
//                                    )
//                                }
//                                val wordList = ArrayList<String>()
////                        WordSuggesterUtil.generatePermutations(arrayListOf(arrayListOf("a", "b"), arrayListOf("c", "d"), arrayListOf()), wordList, 0, "")
//                                WordSuggesterUtil.generatePermutations(interchangeables, wordList, 0, "")
//                                println("log_tag wordlist size: " + wordList.size)
//                                for (s in wordList) {
//                                    println("log_tag: $s")
//                                }
//
//                                sessionWeakReference.get()?.getSentenceSuggestions(Array(1) { TextInfo(dataList[i]) }, 1)
//                                println("log_tag: datalist ij: ${dataList[i][j].toInt()}")
//                            }
//                        }
//                    }

                    activity.runOnUiThread {
                        progressDialog?.dismiss()
                        textList = dataList
                        liveData.value = dataList
                        charImageArray = matArray
                        println("inference called: end")
                    }
                }
            }, activity)

        return liveData
    }

    fun insertDocumentItem(documentItem: DocumentItem, data: ArrayList<String>) {
        documentRepository.insertDocumentItem(documentItem, data)
    }

    fun updateDocumentItem(documentItem: DocumentItem, data: ArrayList<String>) {
        documentRepository.updateDocumentItem(documentItem, data)
    }

    fun getAllAuthors(): LiveData<List<Author>> {
        return authorRepository.getAllAuthors()
    }

    fun insertAuthor() {
        charImageArray?.forEach {
            author?.charactermap?.get(it.character)?.add(it.mat)
        }
        authorRepository.insertAuthor(author!!)
    }
}