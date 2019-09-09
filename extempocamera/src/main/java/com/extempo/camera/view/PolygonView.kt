package com.extempo.camera.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Magnifier
import androidx.annotation.RequiresApi
import com.extempo.camera.R
import java.util.ArrayList
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class PolygonView: FrameLayout {
    private var paint: Paint? = null
    private var pointer1: ImageView? = null
    private var pointer2: ImageView? = null
    private var pointer3: ImageView? = null
    private var pointer4: ImageView? = null
    private var midPointer13: ImageView? = null
    private var midPointer12: ImageView? = null
    private var midPointer34: ImageView? = null
    private var midPointer24: ImageView? = null
    private var polygonView: PolygonView? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttribute: Int) : super(context, attrs, defStyleAttribute)

    init {
        polygonView = this
        pointer1 = getImageView(0, 0)
        pointer2 = getImageView(width, 0)
        pointer3 = getImageView(0, height)
        pointer4 = getImageView(width, height)
        midPointer13 = getImageView(0, height / 2)
        midPointer12 = getImageView(0, width / 2)
        midPointer34 = getImageView(0, height / 2)
        midPointer24 = getImageView(0, height / 2)

        midPointer13?.setOnTouchListener(MidPointTouchListenerImpl(pointer1!!, pointer3!!, polygonView!!))
        midPointer12?.setOnTouchListener(MidPointTouchListenerImpl(pointer1!!, pointer2!!, polygonView!!))
        midPointer34?.setOnTouchListener(MidPointTouchListenerImpl(pointer3!!, pointer4!!, polygonView!!))
        midPointer24?.setOnTouchListener(MidPointTouchListenerImpl(pointer2!!, pointer4!!, polygonView!!))
        pointer1?.setOnTouchListener(TouchListenerImpl(polygonView!!))
        pointer2?.setOnTouchListener(TouchListenerImpl(polygonView!!))
        pointer3?.setOnTouchListener(TouchListenerImpl(polygonView!!))
        pointer4?.setOnTouchListener(TouchListenerImpl(polygonView!!))

        addView(pointer1)
        addView(pointer2)
        addView(midPointer13)
        addView(midPointer12)
        addView(midPointer34)
        addView(midPointer24)
        addView(pointer3)
        addView(pointer4)
        initPaint()
    }

    override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
        return super.onTouchEvent(motionEvent)
    }

    override fun attachViewToParent(child: View, index: Int, params: ViewGroup.LayoutParams) {
        super.attachViewToParent(child, index, params)
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        canvas.drawLine(
            pointer1!!.x + pointer1!!.width / 2,
            pointer1!!.y + pointer1!!.height / 2,
            pointer3!!.x + pointer3!!.width / 2,
            pointer3!!.y + pointer3!!.height / 2,
            paint!!
        )
        canvas.drawLine(
            pointer1!!.x + pointer1!!.width / 2,
            pointer1!!.y + pointer1!!.height / 2,
            pointer2!!.x + pointer2!!.width / 2,
            pointer2!!.y + pointer2!!.height / 2,
            paint!!
        )
        canvas.drawLine(
            pointer2!!.x + pointer2!!.width / 2,
            pointer2!!.y + pointer2!!.height / 2,
            pointer4!!.x + pointer4!!.width / 2,
            pointer4!!.y + pointer4!!.height / 2,
            paint!!
        )
        canvas.drawLine(
            pointer3!!.x + pointer3!!.width / 2,
            pointer3!!.y + pointer3!!.height / 2,
            pointer4!!.x + pointer4!!.width / 2,
            pointer4!!.y + pointer4!!.height / 2,
            paint!!
        )
        midPointer13?.x = pointer3!!.x - (pointer3!!.x - pointer1!!.x) / 2
        midPointer13?.y = pointer3!!.y - (pointer3!!.y - pointer1!!.y) / 2
        midPointer24?.x = pointer4!!.x - (pointer4!!.x - pointer2!!.x) / 2
        midPointer24?.y = pointer4!!.y - (pointer4!!.y - pointer2!!.y) / 2
        midPointer34?.x = pointer4!!.x - (pointer4!!.x - pointer3!!.x) / 2
        midPointer34?.y = pointer4!!.y - (pointer4!!.y - pointer3!!.y) / 2
        midPointer12?.x = pointer2!!.x - (pointer2!!.x - pointer1!!.x) / 2
        midPointer12?.y = pointer2!!.y - (pointer2!!.y - pointer1!!.y) / 2
    }

    fun getPoints(): Map<Int, PointF> {
        val points = ArrayList<PointF>()
        points.add(PointF(pointer1?.x!!, pointer1?.y!!))
        points.add(PointF(pointer2?.x!!, pointer2?.y!!))
        points.add(PointF(pointer3?.x!!, pointer3?.y!!))
        points.add(PointF(pointer4?.x!!, pointer4?.y!!))

        return PolygonUtils.getOrderedPoints(points)
    }

    fun setPoints(pointFMap: Map<Int, PointF>) {
        if (pointFMap.size == 4) {
            setPointsCoordinates(pointFMap)
        }
    }

    fun setMagnifier(magnifier: Magnifier) {
        midPointer13?.setOnTouchListener(MidPointTouchListenerImpl(pointer1!!, pointer3!!, polygonView!!, magnifier))
        midPointer12?.setOnTouchListener(MidPointTouchListenerImpl(pointer1!!, pointer2!!, polygonView!!, magnifier))
        midPointer34?.setOnTouchListener(MidPointTouchListenerImpl(pointer3!!, pointer4!!, polygonView!!, magnifier))
        midPointer24?.setOnTouchListener(MidPointTouchListenerImpl(pointer2!!, pointer4!!, polygonView!!, magnifier))
        pointer1?.setOnTouchListener(TouchListenerImpl(polygonView!!, magnifier))
        pointer2?.setOnTouchListener(TouchListenerImpl(polygonView!!, magnifier))
        pointer3?.setOnTouchListener(TouchListenerImpl(polygonView!!, magnifier))
        pointer4?.setOnTouchListener(TouchListenerImpl(polygonView!!, magnifier))
    }

    private fun setPointsCoordinates(pointFMap1: Map<Int, PointF>) {
        val pointFMap = PolygonUtils.getOrderedPoints(listOf(pointFMap1.getValue(0),
            pointFMap1.getValue(1), pointFMap1.getValue(2), pointFMap1.getValue(3)
        ))
        pointer1?.x = pointFMap[0]?.x!! - getAdjustmentValue()
        pointer1?.y = pointFMap[0]?.y!! - getAdjustmentValue() * 2

        pointer2?.x = pointFMap[1]?.x!! - getAdjustmentValue()
        pointer2?.y = pointFMap[1]?.y!! - getAdjustmentValue() * 2

/*
*       swapped position of 2 & 3 because of order of points
*       order of pointers drawn -> tl, tr, bl, br
*       order of points received -> tl, tr, br, bl [Maintain this order to get correct perspective crop]
*/

        pointer3?.x = pointFMap[3]?.x!! - getAdjustmentValue()
        pointer3?.y = pointFMap[3]?.y!! - getAdjustmentValue() * 2

        pointer4?.x = pointFMap[2]?.x!! - getAdjustmentValue()
        pointer4?.y = pointFMap[2]?.y!! - getAdjustmentValue() * 2
    }

    fun getAdjustmentValue(): Int {
        val adjustment = resources.getDrawable(R.drawable.cropper_bound).intrinsicHeight/2
        PolygonUtils.adjustment = adjustment
        return adjustment
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun getImageView(x: Int, y: Int): ImageView {
        val imageView = ImageView(context)
        val layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        imageView.layoutParams = layoutParams
        imageView.setImageResource(R.drawable.cropper_bound)
        imageView.x = x.toFloat()
        imageView.y = y.toFloat()
        return imageView
    }

    private fun initPaint() {
        paint = Paint()
        paint!!.color = resources.getColor(R.color.white)
        paint!!.strokeWidth = 4f
        paint!!.isAntiAlias = true
    }

    private class MidPointTouchListenerImpl(val mainPointer1: ImageView, val mainPointer2: ImageView, val polygonView: PolygonView):
        OnTouchListener {
        internal var downPT = PointF()
        internal var startPT = PointF()
        internal var magnifier: Magnifier? = null

        constructor(mainPointer1: ImageView, mainPointer2: ImageView, polygonView: PolygonView, magnifier: Magnifier): this(mainPointer1, mainPointer2, polygonView) {
            this.magnifier = magnifier
        }

        @RequiresApi(Build.VERSION_CODES.P)
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            when(event?.action) {
                MotionEvent.ACTION_MOVE -> {
                    val mv = PointF(event.x - downPT.x, event.y - downPT.y)
                    this.magnifier?.show(v?.x!!, v.y)
                    if (abs(mainPointer1.x - mainPointer2.x) > abs(mainPointer1.y - mainPointer2.y)) {
                        if (mainPointer2.y + mv.y + v?.height?.toFloat()!! < polygonView.height && mainPointer2.y + mv.y > 0) {
                            v.x = (startPT.y + mv.y).toInt().toFloat()
                            startPT = PointF(v.x, v.y)
                            mainPointer2.y = (mainPointer2.y + mv.y).toInt().toFloat()
                        }
                        if (mainPointer1.y + mv.y + v.height.toFloat() < polygonView.height && mainPointer1.y + mv.y > 0) {
                            v.x = (startPT.y + mv.y).toInt().toFloat()
                            startPT = PointF(v.x, v.y)
                            mainPointer1.y = (mainPointer1.y + mv.y).toInt().toFloat()
                        }
                    } else {
                        if (mainPointer2.x + mv.x + v?.width?.toFloat()!! < polygonView.width && mainPointer2.x + mv.x > 0) {
                            v.x = (startPT.x + mv.x).toInt().toFloat()
                            startPT = PointF(v.x, v.y)
                            mainPointer2.x = (mainPointer2.x + mv.x).toInt().toFloat()
                        }
                        if (mainPointer1.x + mv.x + v.width.toFloat() < polygonView.width && mainPointer1.x + mv.x > 0) {
                            v.x = (startPT.x + mv.x).toInt().toFloat()
                            startPT = PointF(v.x, v.y)
                            mainPointer1.x = (mainPointer1.x + mv.x).toInt().toFloat()
                        }
                    }
                }

                MotionEvent.ACTION_DOWN -> {
                    downPT.x = event.x
                    downPT.y = event.y
                    startPT = PointF(v?.x!!, v.y)
                    this.magnifier?.show(v.x, v.y)
                }

                MotionEvent.ACTION_UP -> {
                    var color = if (PolygonUtils.isValidShape(polygonView.getPoints())) {
                        polygonView.resources.getColor(R.color.white)
                    } else {
                        polygonView.resources.getColor(R.color.red)
                    }
                    polygonView.paint?.color = color
                    this.magnifier?.dismiss()
                }
            }
            polygonView.invalidate()

            return true
        }
    }

    private class TouchListenerImpl(val polygonView: PolygonView): OnTouchListener {
        constructor(polygonView: PolygonView, magnifier: Magnifier): this(polygonView) {
            this.magnifier = magnifier
        }
        internal var downPT = PointF()
        internal var startPT = PointF()
        internal var magnifier: Magnifier? = null

        @RequiresApi(Build.VERSION_CODES.P)
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            when (event?.action) {
                MotionEvent.ACTION_MOVE -> {

                    val mv = PointF(event.x - downPT.x, event.y - downPT.y)
                    if (startPT.x + mv.x + v?.width?.toFloat()!! < polygonView.width && startPT.y + mv.y + v.getHeight().toFloat() < polygonView.height && startPT.x + mv.x > 0 && startPT.y + mv.y > 0) {
                        v.x = (startPT.x + mv.x).toInt().toFloat()
                        v.y = (startPT.y + mv.y).toInt().toFloat()
                        startPT = PointF(v.x, v.y)
                        this.magnifier?.show(v.x, v.y)
                    }
                }

                MotionEvent.ACTION_DOWN -> {
                    downPT.x = event.x
                    downPT.y = event.y
                    startPT = PointF(v?.x!!, v.y)
                    this.magnifier?.show(v.x, v.y)
                }

                MotionEvent.ACTION_UP -> {
                    this.magnifier?.dismiss()
                    var color = if (PolygonUtils.isValidShape(polygonView.getPoints())) {
                        polygonView.resources.getColor(R.color.white)
                    } else {
                        polygonView.resources.getColor(R.color.red)
                    }
                    polygonView.paint?.color = color
                }
            }
            polygonView.invalidate()

            return true
        }
    }

    object PolygonUtils {
        var adjustment = 20

        fun isValidShape(pointFMap: Map<Int, PointF>): Boolean {
            return pointFMap.size == 4
        }

        fun getOrderedPoints(points: List<PointF>): Map<Int, PointF> {
            var maxx: Float = -1.0F
            var maxy: Float = -1.0F
            var minx = 10000.0F
            var miny = 10000.0F

            for (pointF in points) {
                maxx = max(maxx, pointF.x)
                minx = min(minx, pointF.x)
                maxy = max(maxy, pointF.y)
                miny = min(miny, pointF.y)
            }

            val centerPoint = PointF((minx+maxx)/2, (miny+maxy)/2)
            val orderedPoints = HashMap<Int, PointF>()
            for (pointF in points) {
                if (pointF.x < centerPoint.x && pointF.y < centerPoint.y) {
                    orderedPoints[0] = pointF
                }
                if (pointF.x > centerPoint.x && pointF.y < centerPoint.y) {
                    orderedPoints[1] = pointF
                }
                if (pointF.x > centerPoint.x && pointF.y > centerPoint.y) {
                    orderedPoints[2] = pointF
                }
                if (pointF.x < centerPoint.x && pointF.y > centerPoint.y) {
                    orderedPoints[3] = pointF
                }
            }
            return orderedPoints
        }
    }
}