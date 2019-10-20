package com.extempo.camera.view


import android.Manifest
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.RestrictionsManager.RESULT_ERROR
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.hardware.Camera
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Magnifier
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.extempo.camera.R
import com.extempo.camera.databinding.ActivityCameraBinding
import com.extempo.camera.model.CameraModel.getCameraInstance
import com.extempo.camera.model.CoordinateAnimator
import com.extempo.camera.model.listeners.OnPictureTakenListener
import com.extempo.camera.utilities.BitmapUtils
import com.extempo.camera.utilities.CameraUtility
import com.extempo.camera.utilities.InjectorUtils
import com.extempo.camera.viewmodel.CameraViewModel
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.android.synthetic.main.activity_camera.cropButton
import kotlinx.android.synthetic.main.activity_camera.polygonOverlayView
import kotlinx.android.synthetic.main.activity_camera.rotateButton
import org.opencv.core.Point
import java.io.File
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class CameraActivity : AppCompatActivity() {
    private var mCamera: Camera? = null
    private var mPreview: CameraPreview? = null
    private var imageBitmap: Bitmap? = null
    private var destinationUri: Uri? = null
    private lateinit var binding: ActivityCameraBinding
    private lateinit var cameraViewModel: CameraViewModel
    private var isShowing = true
    var isSaving = false
    var flashOn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkHardware()
        initializeUI()
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            CAMERA_PERMISSION -> {
                var i = 0
                val len = permissions.size
                while (i < len) {
                    val permission = permissions[i]

                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        val showRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                            this, permission
                        )

                        if (showRationale) {
                            showAlert()
                        } else if (!showRationale) {
                            saveToPreferences(this@CameraActivity, ALLOW_KEY, true)
                        }
                    }
                    i++
                }
            }
        }
    }


    private fun initializeListeners() {
        mPreview.let {
            cameraPreviewFrameLayout.setOnTouchListener { _, motionEvent ->
                if (motionEvent.action == MotionEvent.ACTION_DOWN){
                    val offset = resources.getDrawable(R.drawable.capture_button).intrinsicHeight/2
                    if (motionEvent.x - offset < 0) {
                        autoFocusImageView.x = 0f
                    } else {
                        autoFocusImageView.x = motionEvent.x - offset
                    }
                    if (motionEvent.x - offset < 0) {
                        autoFocusImageView.y = 0f
                    } else {
                        autoFocusImageView.y = motionEvent.y - offset
                    }

                    autoFocusImageView.visibility = View.VISIBLE

                    if (mCamera != null && it != null) {
                        mCamera?.autoFocus { success, camera ->
                            autoFocusImageView.visibility = View.GONE
                            if (success) {
//                            takePicture()
                            }
                        }
                    }
                }
                return@setOnTouchListener true
            }

//            capturedImageLayoutContainer.setOnClickListener {
//                if (isShowing) {
//                    buttonBar.visibility = View.GONE
//                    isShowing = false
//                } else {
//                    buttonBar.visibility = View.VISIBLE
//                    isShowing = true
//                }
//            }

            flashImageButton.setOnClickListener {
                var params = mCamera!!.parameters
                if (flashOn) {
                    params.flashMode = Camera.Parameters.FLASH_MODE_OFF
                    mCamera!!.parameters = params
                    flashImageButton.setImageResource(R.drawable.ic_zap)
                    flashOn = false
                } else {
                    params.flashMode = Camera.Parameters.FLASH_MODE_TORCH
                    mCamera!!.parameters = params
                    flashImageButton.setImageResource(R.drawable.ic_zap_off)
                    flashOn = true
                }
                cameraPreviewFrameLayout.callOnClick()
            }

            captureImageButton.setOnClickListener {
                mPreview?.visibility = View.GONE
                captureImageButton.visibility = View.GONE
                capturedImageView.visibility = View.VISIBLE
                capturedImageView.scaleType = ImageView.ScaleType.FIT_XY
                takePicture()
                cropButton.visibility = View.VISIBLE
                rotateButton.visibility = View.VISIBLE
                repeatButton.visibility = View.VISIBLE
                cancelButton.visibility = View.VISIBLE
            }

            cropButton.setOnClickListener {
                BitmapUtils.cropPicture(imageBitmap!!, polygonOverlayView).also {
                    if (it != null) {
                        val pointFMap = polygonOverlayView.getPoints()
                        val tl = Point(pointFMap[0]?.x!!.toDouble() + getAdjustmentValue(), pointFMap[0]?.y!!.toDouble() + getAdjustmentValue() * 2)
                        val coordinateAnimator = ValueAnimator.ofObject(CoordinateAnimator(), arrayOf(0,0, imageBitmap!!.height, imageBitmap!!.width), arrayOf((tl.y).toInt(), (tl.x).toInt(), it.height, it.width))
                        coordinateAnimator.addUpdateListener { valueAnimator ->
                            val values = valueAnimator.animatedValue as Array<Int>
                            val bmp = Bitmap.createBitmap(imageBitmap!!, values[1], values[0], values[3], values[2])
                            binding.imageBitmap = bmp
                        }

                        if (isShowing) {
                            buttonBar.visibility = View.GONE
                            isShowing = false
                        }

                        coordinateAnimator.duration = 1000
                        coordinateAnimator.start()

                        Handler().postDelayed({
                            imageBitmap = it
                            binding.imageBitmap = it
                            if (!isShowing) {
                                buttonBar.visibility = View.VISIBLE
                                isShowing = true
                            }
                        }, 1000)

                        polygonOverlayView.visibility = View.GONE
                        cropButton.visibility = View.GONE
                        acceptButton.visibility = View.VISIBLE
                        cancelButton.visibility = View.VISIBLE
                        capturedImageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
                    }
                }
            }

            rotateButton.setOnClickListener {
                binding.imageBitmap = BitmapUtils.rotateImage(binding.imageBitmap, 90).also {
                    imageBitmap = it!!
                    if (polygonOverlayView.visibility != View.GONE) {
                        val bmp = it.let { BitmapUtils.scaleBitmap(it, 1080) }
                        imageBitmap = bmp
                        cameraViewModel.getBounds(bmp, polygonOverlayView)
                    }
                }
            }

            acceptButton.setOnClickListener {
                saveBitmap(imageBitmap)
                finish()
            }

            cancelButton.setOnClickListener{
                setResultUri(null)
                finish()
            }

            repeatButton.setOnClickListener {
                cropButton.visibility = View.GONE
                rotateButton.visibility = View.GONE
                acceptButton.visibility = View.GONE
                cancelButton.visibility = View.GONE
                repeatButton.visibility = View.GONE
                mPreview?.visibility = View.VISIBLE
                captureImageButton.visibility = View.VISIBLE
                capturedImageView.visibility = View.GONE
                polygonOverlayView.visibility = View.GONE
            }
        }
    }

    private fun getAdjustmentValue(): Int {
        adjustment = resources.getDrawable(R.drawable.cropper_bound).intrinsicHeight/2
        return adjustment
    }

    private fun initializeUI() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (getFromPref(this, ALLOW_KEY)) {
                showSettingsAlert()
            } else if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA)

                != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.CAMERA)) {
                    showAlert()
                } else {
                    ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.CAMERA),
                        CAMERA_PERMISSION
                    )
                }
            }
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            showAlertStorage()
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            destinationUri = intent.extras?.getParcelable(MediaStore.EXTRA_OUTPUT) as Uri?
            openCamera()
            initializeListeners()
            if (Build.VERSION.SDK_INT >= 28) {
//                val magnifier = Magnifier.Builder(capturedImageLayoutContainer).setSize(80, 80).build()
                polygonOverlayView.setMagnifier(Magnifier(capturedImageLayoutContainer))
            }
        }
    }

    private fun checkHardware() {
        val isDeviceHasCamera = CameraUtility.checkCameraHardware(this@CameraActivity)
        if (!isDeviceHasCamera) {
            finish()
            Toast.makeText(this@CameraActivity, "There is no Camera!!", Toast.LENGTH_LONG).show()
            return
        }
    }

    private fun saveToPreferences(context: Context, key: String, allowed: Boolean?) {
        val myPrefs = context.getSharedPreferences(
            CAMERA_PREF,
            Context.MODE_PRIVATE
        )
        val prefsEditor = myPrefs.edit()
        prefsEditor.putBoolean(key, allowed!!)
        prefsEditor.apply()
    }

    private fun getFromPref(context: Context, key: String): Boolean {
        val myPrefs = context.getSharedPreferences(
            CAMERA_PREF,
            Context.MODE_PRIVATE
        )
        return myPrefs.getBoolean(key, false)
    }

    private fun showAlert() {
        val alertDialog = AlertDialog.Builder(this@CameraActivity).create()
        alertDialog.setTitle("Alert")
        alertDialog.setMessage("App needs to access the Camera.")

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW") { dialog, _ ->
            dialog.dismiss()
            finish()
        }

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "ALLOW", fun(dialog: DialogInterface, _: Int) {
            dialog.dismiss()
            ActivityCompat.requestPermissions(
                this@CameraActivity,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION
            )
        }
        )
        alertDialog.show()
    }

    private fun showAlertStorage() {
        val alertDialog = AlertDialog.Builder(this@CameraActivity).create()
        alertDialog.setTitle("Alert")
        alertDialog.setMessage("App needs to access the Storage.")

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW") { dialog, _ ->
            dialog.dismiss()
            finish()
        }

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "ALLOW", fun(dialog: DialogInterface, _: Int) {
            dialog.dismiss()
            ActivityCompat.requestPermissions(
                this@CameraActivity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_PERMISSION
            )
        }
        )
        alertDialog.show()
    }

    private fun showSettingsAlert() {
        val alertDialog = AlertDialog.Builder(this@CameraActivity).create()
        alertDialog.setTitle("Alert")
        alertDialog.setMessage("App needs to access the Camera.")

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW"
        ) { dialog, _ ->
            dialog.dismiss()
            finish()
        }

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "SETTINGS") { dialog, _ ->
            dialog.dismiss()
            startInstalledAppDetailsActivity(this@CameraActivity)
        }

        alertDialog.show()
    }

    private fun startInstalledAppDetailsActivity(context: Activity?) {
        if (context == null) {
            return
        }

        val i = Intent()
        i.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        i.addCategory(Intent.CATEGORY_DEFAULT)
        i.data = Uri.parse("package:" + context.packageName)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        context.startActivity(i)
    }

    private fun openCamera() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_camera)
        layoutContainer.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN
        val factory = InjectorUtils.provideCameraViewModelFactory()
        cameraViewModel = ViewModelProviders.of(this, factory).get(CameraViewModel::class.java)

        mCamera = getCameraInstance()

        mPreview = mCamera?.let {
            CameraPreview(this, it)
        }

        mPreview?.also {
            cameraPreviewFrameLayout.addView(it)
        }
    }

    private fun takePicture() {
        mPreview?.takePicture(object: OnPictureTakenListener {
            override fun onPicReceived(bitmap: Bitmap?) {
                if (bitmap == null) {
                    return
                }
                bitmap.let {image ->
//                    val scaledBitmap = BitmapUtils.scaleBitmap(image, mPreview?.height!!)
                    cameraViewModel.getBounds(image, polygonOverlayView)
                    binding.imageBitmap = image.also {
                        imageBitmap = it
                    }
                }
            }

        })
    }

    private fun saveBitmap(bitmap: Bitmap?) {
        isSaving = true

        if (destinationUri != null) {
            var outputStream: OutputStream? = null
            try {
                outputStream = contentResolver.openOutputStream(destinationUri!!)
                if (outputStream != null) {
                    bitmap?.compress(
                        Bitmap.CompressFormat.JPEG, 100, outputStream)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                outputStream?.close()
            }

            setResultUri(destinationUri)
        } else {
            var photoFile: File? = null
            try {
                photoFile = newImageFile()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Try Again", Toast.LENGTH_SHORT).show()
            }

            if (photoFile != null) {
                destinationUri = FileProvider.getUriForFile(this, "com.extempo.camera.fileprovider", photoFile)
            }

            var outputStream: OutputStream? = null
            try {
                outputStream = contentResolver.openOutputStream(destinationUri!!)
                if (outputStream != null) {
                    bitmap?.compress(
                        Bitmap.CompressFormat.JPEG, 100, outputStream)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                outputStream?.close()
            }

            setResultUri(destinationUri)
        }

        isSaving = false
    }

    private fun newImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        var storageDir: File? = File(Environment.getExternalStorageDirectory(), "Type Scan")
        if (!storageDir!!.exists()) {
            storageDir.mkdirs()
        }
        if (storageDir == null) {
            storageDir = filesDir
        }
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    private fun setResultUri(uri: Uri?) {
        if (uri == null) {
            setResult(RESULT_ERROR)
        } else {
            setResult(RESULT_OK, Intent().putExtra(MediaStore.EXTRA_OUTPUT, uri))
        }
    }

    companion object {
        const val CAMERA_PERMISSION = 4
        const val STORAGE_PERMISSION = 5
        const val ALLOW_KEY = "ALLOWED"
        const val CAMERA_PREF = "camera_pref"
        var adjustment = 0

        @JvmStatic
        @BindingAdapter("bind:imageBitmap")
        fun loadImage(iv: ImageView, bitmap: Bitmap?) {
            iv.setImageBitmap(bitmap)
        }
    }
}
