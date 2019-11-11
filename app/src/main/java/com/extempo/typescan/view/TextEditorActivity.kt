package com.extempo.typescan.view

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.extempo.typescan.R
import com.extempo.typescan.databinding.ActivityTextEditorBinding
import com.extempo.typescan.model.DocumentItem
import com.extempo.typescan.utilities.InjectorUtils
import com.extempo.typescan.viewmodel.TextEditorActivityViewModel
import kotlinx.android.synthetic.main.activity_text_editor.*
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class TextEditorActivity : AppCompatActivity() {

    var binding: ActivityTextEditorBinding? = null
    var viewModel: TextEditorActivityViewModel? = null
    var isNew: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_editor)
        initializeUI()
    }

    private fun initializeUI() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_text_editor)
        val factory = InjectorUtils.provideTextEditorActivityViewModelFactory(this)
        viewModel = ViewModelProviders.of(this, factory).get(TextEditorActivityViewModel::class.java)

        isNew = intent.getBooleanExtra(HomeActivity.TEXT_EDITOR_NEW, false)

        if (isNew!!) {
            val result = intent.getParcelableExtra<Uri>(HomeActivity.TEXT_EDITOR_DATA)
            Glide.with(this)
                .asBitmap()
                .load(result)
                .into(object : CustomTarget<Bitmap>(){
                    @SuppressLint("RestrictedApi")
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        val data = viewModel?.runInference(resource)
                        data?.observe(this@TextEditorActivity, Observer { dataList ->
                            var dataString = ""
                            dataList.forEach { dataString += "$it\n" }
                            binding?.documentData = dataString
                        })
                        viewModel?.documentItem = DocumentItem("", "")
                        binding?.documentItem = viewModel?.documentItem
                    }
                    override fun onLoadCleared(placeholder: Drawable?) {

                    }
                })
        } else {
            var dataString = ""
            val documentItem = intent.getSerializableExtra(HomeActivity.TEXT_EDITOR_DOCUMENT_ITEM) as DocumentItem
            viewModel?.documentItem = documentItem
            binding?.documentItem = viewModel?.documentItem
//            val dir = File(this.filesDir.path, "text")
            if (this.filesDir.exists()) {
                try {
                    val file = File(this.filesDir, documentItem.filename + ".txt")
                    val fileInputStream = FileInputStream(file)
                    val inputStreamReader = InputStreamReader(fileInputStream)

                    val dataList = inputStreamReader.readLines()

                    dataList.forEach {data->
                        println("log_tag" + data)
                        dataString += data
                    }
                    binding?.documentData = dataString
                    viewModel?.textList  = dataList as ArrayList<String>
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            } else {
                Toast.makeText(this, "Error reading file", Toast.LENGTH_SHORT).show()
            }
        }
        initializeListeners()
    }

    private fun initializeListeners() {
        text_editor_save_button.setOnClickListener {
            val dataList = ArrayList<String>()
            val tokens = StringTokenizer(text_editor_content_text.text.toString(), "\n\r")
            while(tokens.hasMoreTokens()) {
                dataList.add(tokens.nextToken())
            }
            viewModel?.textList = dataList
            viewModel?.documentItem?.let {docItem->
                if (text_editor_author.text.toString().isNotBlank() && text_editor_title.text.toString().isNotBlank()) {
                    viewModel?.textList?.let {textList->
                        docItem.author = text_editor_author.text.toString()
                        docItem.title = text_editor_title.text.toString()
                        docItem.generateFilename()
                        binding?.documentItem = docItem
                        if (isNew!!) {
                            viewModel?.insertDocumentItem(docItem, textList).also {
                                finish()
                            }
                        } else {
                            viewModel?.updateDocumentItem(docItem, textList).also {
                                finish()
                            }
                        }
                    }
                }
            }
        }

        text_editor_cancel_button.setOnClickListener {
            finish()
        }
    }

    companion object {
        @JvmStatic
        @BindingAdapter("bind:editableText")
        fun loadDocumentContent(et: EditText, data: String?) {
            data?.let {
                println("log_tag: documentContent $data")
                et.setText(it, TextView.BufferType.EDITABLE)
            }
        }

        @JvmStatic
        @BindingAdapter("bind:author")
        fun loadAuthor(et: EditText, data: DocumentItem?) {
            data?.let {
                et.setText(it.author, TextView.BufferType.EDITABLE)
            }
        }

        @JvmStatic
        @BindingAdapter("bind:title")
        fun loadtitle(et: EditText, data: DocumentItem?) {
            data?.let {
                et.setText(it.title, TextView.BufferType.EDITABLE)
            }
        }
    }
}
