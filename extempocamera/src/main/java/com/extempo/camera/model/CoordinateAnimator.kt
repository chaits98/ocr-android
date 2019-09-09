package com.extempo.camera.model

import android.animation.TypeEvaluator
import kotlin.math.roundToInt

class CoordinateAnimator: TypeEvaluator<Array<Int>> {
    override fun evaluate(fraction: Float, startValues: Array<Int>?, endValues: Array<Int>?): Array<Int> {
        if (startValues?.size != endValues?.size) throw ArrayIndexOutOfBoundsException()
        val returnValues = arrayOf(0, 0, 0, 0)

        returnValues[0] = (startValues?.get(0)!! + (endValues?.get(0)!!) * fraction).roundToInt()
        returnValues[1] = (startValues[1] + (endValues[1]) * fraction).roundToInt()
        returnValues[2] = (startValues[2] - (startValues[2] - endValues[2]) * fraction).roundToInt()
        returnValues[3] = (startValues[3] - (startValues[3] - endValues[3]) * fraction).roundToInt()
        return returnValues
    }

}