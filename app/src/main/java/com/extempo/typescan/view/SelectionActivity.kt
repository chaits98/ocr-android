package com.extempo.typescan.view

import android.app.Activity
import android.content.Intent
import android.content.RestrictionsManager.RESULT_ERROR
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.extempo.camera.view.CameraActivity
import com.extempo.opticalcharacterrecognizer.model.listeners.InferenceListener
import com.extempo.opticalcharacterrecognizer.utilities.OpticalCharacterDetector
import com.extempo.typescan.R
import com.extempo.typescan.databinding.ActivityHomeBinding
import com.extempo.typescan.utilities.InjectorUtils
import com.extempo.typescan.utilities.ModuleLoader
import com.extempo.typescan.viewmodel.SelectionActivityViewModel
import kotlinx.android.synthetic.main.activity_home.*


class SelectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var selectionViewModel: SelectionActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeUI()
        setListeners()
    }

    private fun initializeUI() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        val factory = InjectorUtils.provideSelectionActivityViewModelFactory()
        selectionViewModel = ViewModelProviders.of(this, factory).get(SelectionActivityViewModel::class.java)
    }

    private fun setListeners() {
        openCameraButton.setOnClickListener {
            val cameraActivity = Intent(this, CameraActivity::class.java)
            startActivityForResult(cameraActivity, CAMERA_ACTIVITY)
        }

        runInferenceButton.setOnClickListener {
            selectionViewModel.capturedImageBitmap?.let {
                OpticalCharacterDetector.findAlphabets(it,
                    object : InferenceListener {
                        override fun started(bitmap: Bitmap) {
                            // show Activity Indicator
                        }

                        override fun finished(bitmap: Bitmap) {
                            binding.croppedImageBitmap = bitmap
                            // stop activity Indicator
                            openCameraButton.visibility = View.VISIBLE
                            runInferenceButton.visibility = View.GONE
                        }
                    })
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CAMERA_ACTIVITY) {
            if(resultCode == Activity.RESULT_OK){
                if (data?.hasExtra(MediaStore.EXTRA_OUTPUT)!!) {
                    val result = data.getParcelableExtra(MediaStore.EXTRA_OUTPUT) as Uri
                    Glide.with(this)
                        .asBitmap()
                        .load(result)
                        .into(object : CustomTarget<Bitmap>(){
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                binding.croppedImageBitmap = resource
                                selectionViewModel.capturedImageUri = result
                                selectionViewModel.capturedImageBitmap = resource
                                runInferenceButton.visibility = View.VISIBLE
                                openCameraButton.visibility = View.GONE
                            }
                            override fun onLoadCleared(placeholder: Drawable?) {

                            }
                        })
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {

            }
            if (resultCode == RESULT_ERROR) {

            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        init {
            ModuleLoader.load()
        }

        const val CAMERA_ACTIVITY = 70

        @JvmStatic
        @BindingAdapter("bind:imageBitmap")
        fun loadImage(iv: ImageView, bitmap: Bitmap?) {
            iv.setImageBitmap(bitmap)
        }
    }
}
