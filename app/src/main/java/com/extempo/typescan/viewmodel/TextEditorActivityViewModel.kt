package com.extempo.typescan.viewmodel

import android.annotation.SuppressLint
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

class TextEditorActivityViewModel(private val documentRepository: DocumentRepository, private val authorRepository: AuthorRepository) : ViewModel() {
    var textList: ArrayList<String>? = null
    var documentItem: DocumentItem? = null
    var author: Author? = null
    var charImageArray: ArrayList<CharImage>? = null

    fun runInference(capturedImageBitmap: Bitmap, context: Context, sessionWeakReference: WeakReference<SpellCheckerSession>): LiveData<ArrayList<String>> {
        val liveData = MutableLiveData<ArrayList<String>>()
        OpticalCharacterDetector.findAlphabets2(capturedImageBitmap,
            object : InferenceListener {
                override fun started() {
                    // show Activity Indicator
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
                    textList = dataList
                    liveData.value = dataList
                    charImageArray = matArray
                }
            }, context)

        return liveData
    }

    fun insertDocumentItem(documentItem: DocumentItem, data: ArrayList<String>) {
        documentRepository.insertDocumentItem(documentItem, data)
    }

    fun updateDocumentItem(documentItem: DocumentItem, data: ArrayList<String>) {
        documentRepository.updateDocumentItem(documentItem, data)
    }

    fun getAllAuthors() {
        println("log_tag: authors: " + authorRepository.getAllAuthors())
    }

    fun insertAuthor() {
        charImageArray?.forEach {
            author?.charactermap?.get(it.character)?.add(it.mat)
        }
        authorRepository.insertAuthor(author!!)
    }
}