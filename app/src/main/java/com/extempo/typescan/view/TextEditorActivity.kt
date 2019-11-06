package com.extempo.typescan.view

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.extempo.typescan.R
import kotlinx.android.synthetic.main.activity_text_editor.*

class TextEditorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_editor)
        initializeUI()
    }

    fun initializeUI() {
        val contentTextView = text_editor_content_text
    }

    companion object {
        @JvmStatic
        @BindingAdapter("bind:editableText")
        fun loadDocumentContent(et: EditText, data: String) {
            et.setText(data, TextView.BufferType.EDITABLE)
        }
    }
}
