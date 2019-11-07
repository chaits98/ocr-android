package com.extempo.typescan.view

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
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
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader

class TextEditorActivity : AppCompatActivity() {

    var binding: ActivityTextEditorBinding? = null
    var viewModel: TextEditorActivityViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_editor)
        initializeUI()
    }

    fun initializeUI() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_text_editor)
        val factory = InjectorUtils.provideTextEditorActivityViewModelFactory(this)
        viewModel = ViewModelProviders.of(this, factory).get(TextEditorActivityViewModel::class.java)

        val isNew = intent.getBooleanExtra(HomeActivity.TEXT_EDITOR_NEW, false)

        if (isNew) {
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
                    }
                    override fun onLoadCleared(placeholder: Drawable?) {

                    }
                })
        } else {
            var dataString = ""
            val documentItem = intent.getSerializableExtra(HomeActivity.TEXT_EDITOR_DOCUMENT_ITEM) as DocumentItem
            val file = File(this.filesDir, documentItem.filename)
            val fileInputStream = FileInputStream(file)
            val inputStreamReader = InputStreamReader(fileInputStream)
            inputStreamReader.forEachLine {data->
                dataString += data
            }
        }
    }

    companion object {
        @JvmStatic
        @BindingAdapter("bind:editableText")
        fun loadDocumentContent(et: EditText, data: String?) {
            data?.let {
                et.setText(it, TextView.BufferType.EDITABLE)
            }
        }
    }
}
