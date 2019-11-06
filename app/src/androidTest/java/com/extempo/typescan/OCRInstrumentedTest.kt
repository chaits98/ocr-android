package com.extempo.typescan

import android.graphics.Bitmap
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.extempo.opticalcharacterrecognizer.utilities.OpticalCharacterDetector

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import android.graphics.BitmapFactory
import com.extempo.opticalcharacterrecognizer.R
import com.extempo.opticalcharacterrecognizer.model.listeners.InferenceListener
import com.extempo.typescan.utilities.ModuleLoader


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class OCRInstrumentedTest {
    @Test
    fun segmentImage() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        ModuleLoader.load()
        val icon = BitmapFactory.decodeResource(
            appContext.resources,
            R.mipmap.the_test_foreground
        )
        OpticalCharacterDetector.findAlphabets(icon, object: InferenceListener {
            override fun started() {

            }

            override fun finished(dataList: ArrayList<String>) {

            }
        })
    }
}
