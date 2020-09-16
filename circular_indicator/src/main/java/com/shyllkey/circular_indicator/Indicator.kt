package com.shyllkey.circular_indicator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

class Indicator : View {
    private var mPaint: Paint? = null
    var strokeWidth = 0f
    var strokeColor = 0
    private var mRect: RectF? = null
    private var mStrokeCap: String? = null
    var startAngle = 0
    var endAngle = 0
    var sweepAngle = 0
    var startValue = 0
    private var mEndValue = 0
    private var mValue = 0
    private var mPointAngle = 0.0
    private var mPoint = 0
    var pointSize = 0
    var pointStartColor = 0
    var pointEndColor = 0
    var dividerColor = 0
    private var mDividerSize = 0
    private var mDividerStepAngle = 0
    private var mDividersCount = 0
    var isDividerDrawFirst = false
    var isDividerDrawLast = false
    private var mLowColor = 0
    private var mMediumColor = 0
    private var mHighColor = 0
    private var mPointerColor = 0
    private var mPointerSize = 0
    private var mPointerAngle = 0.0
    private var mShowPointer = false
    private var mRotation = 0f
    private var mPoint1 = 0f
    private var mPoint2 = 0f
    private var mPoint3 = 0f
    private var mPoint4 = 0f

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CustomGauge, 0, 0)

        // stroke style
        strokeWidth = a.getDimension(R.styleable.CustomGauge_gaugeStrokeWidth, 10f)
        strokeColor = a.getColor(
            R.styleable.CustomGauge_gaugeStrokeColor,
            ContextCompat.getColor(context, android.R.color.darker_gray)
        )
        strokeCap = a.getString(R.styleable.CustomGauge_gaugeStrokeCap)

        // angle start and sweep (opposite direction 0, 270, 180, 90)
        startAngle = a.getInt(R.styleable.CustomGauge_gaugeStartAngle, 0)
        endAngle = a.getInt(R.styleable.CustomGauge_gaugeEndAngle, 0)
        sweepAngle = a.getInt(R.styleable.CustomGauge_gaugeSweepAngle, 360)

        // scale (from mStartValue to mEndValue)
        startValue = a.getInt(R.styleable.CustomGauge_gaugeStartValue, 0)
        endValue = a.getInt(R.styleable.CustomGauge_gaugeEndValue, 1000) //
        setPositionValue(a.getInt(R.styleable.CustomGauge_gaugePosition, 270)) //

        // pointer size and color
        pointSize = a.getInt(R.styleable.CustomGauge_gaugePointSize, 0)
        pointStartColor = a.getColor(
            R.styleable.CustomGauge_gaugePointStartColor,
            ContextCompat.getColor(context, android.R.color.white)
        )
        pointEndColor = a.getColor(
            R.styleable.CustomGauge_gaugePointEndColor,
            ContextCompat.getColor(context, android.R.color.white)
        )
        setLowColor(
            a.getColor(
                R.styleable.CustomGauge_gaugeLowColor,
                ContextCompat.getColor(context, android.R.color.white)
            )
        ) //
        setMediumEndColor(
            a.getColor(
                R.styleable.CustomGauge_gaugeMediumColor,
                ContextCompat.getColor(context, android.R.color.white)
            )
        )
        setHighColor(
            a.getColor(
                R.styleable.CustomGauge_gaugeHighColor,
                ContextCompat.getColor(context, android.R.color.white)
            )
        )
        setPointerColor(
            a.getColor(
                R.styleable.CustomGauge_gaugePointerColor,
                ContextCompat.getColor(context, android.R.color.white)
            )
        )
        setPointerWidth(
            a.getColor(
                R.styleable.CustomGauge_gaugePointerWidth,
                ContextCompat.getColor(context, android.R.color.white)
            )
        )

        // divider options
        val dividerSize = a.getInt(R.styleable.CustomGauge_gaugeDividerSize, 0)
        dividerColor = a.getColor(
            R.styleable.CustomGauge_gaugeDividerColor,
            ContextCompat.getColor(context, android.R.color.white)
        )
        val dividerStep = a.getInt(R.styleable.CustomGauge_gaugeDividerStep, 0)
//        dividerDrawFirst = a.getBoolean(R.styleable.CustomGauge_gaugeDividerDrawFirst, true)
//        setDividerDrawLast(a.getBoolean(R.styleable.CustomGauge_gaugeDividerDrawLast, true)
        setShowPointer(a.getBoolean(R.styleable.CustomGauge_gaugeShowPointer, false)) //

        // calculating one point sweep
        mPointAngle = Math.abs(sweepAngle).toDouble() / (mEndValue - startValue)

        // calculating divider step
        if (dividerSize > 0) {
            mDividerSize = sweepAngle / (Math.abs(mEndValue - startValue) / dividerSize)
            mDividersCount = 100 / dividerStep
            mDividerStepAngle = sweepAngle / mDividersCount
        }
        mPointerAngle = if (mPointerAngle > 39 && mPointerAngle < 45) 38.0 else mPointerAngle
        mPointerAngle = if (mPointerAngle > 139 && mPointerAngle < 145) 138.0 else mPointerAngle
        mPointerAngle += 270
        mPointerAngle = if (mPointerAngle < 270) 270.0 else if (mPointerAngle > 450) 450.0 else mPointerAngle
        mRotation = 90f
        mPoint1 = startAngle * 0.20f
        mPoint2 = startAngle + mPoint1 + 10
        mPoint3 = startAngle * 0.50f
        mPoint4 = mPoint2 + mPoint3
        a.recycle()
        init()
    }

    private fun setShowPointer(value: Boolean) {
        mShowPointer = value
    }

    private fun setPositionValue(value: Int) {
        mPointerAngle = value.toDouble()
    }

    private fun setPointerWidth(color: Int) {
        mPointerSize = color
    }

    private fun setPointerColor(color: Int) {
        mPointerColor = color
    }

    private fun setLowColor(color: Int) {
        mLowColor = color
    }

    private fun setMediumEndColor(color: Int) {
        mMediumColor = color
    }

    private fun setHighColor(color: Int) {
        mHighColor = color
    }

    private fun init() {
        //main Paint
        mPaint = Paint()
        mPaint!!.color = strokeColor
        mPaint!!.strokeWidth = strokeWidth
        mPaint!!.isAntiAlias = true
        if (!TextUtils.isEmpty(mStrokeCap)) {
            if (mStrokeCap == "BUTT") mPaint!!.strokeCap =
                Paint.Cap.BUTT else if (mStrokeCap == "ROUND") mPaint!!.strokeCap = Paint.Cap.ROUND
        } else mPaint!!.strokeCap = Paint.Cap.BUTT
        mPaint!!.style = Paint.Style.STROKE
        mRect = RectF()
        mValue = startValue
        mPoint = startAngle
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        val padding = strokeWidth
        val size = if (width < height) width.toFloat() else height.toFloat()
        val width = size - 2 * padding
        val height = size - 2 * padding

//        float radius = (width > height ? width/2 : height/2);
        val radius = if (width < height) width / 2 else height / 2
        val rectLeft = (getWidth() - 2 * padding) / 2 - radius + padding
        val rectTop = (getHeight() - 2 * padding) / 2 - radius + padding
        val rectRight = (getWidth() - 2 * padding) / 2 - radius + padding + width
        val rectBottom =
            (getHeight() - 2 * padding) / 2 - radius + padding + height
        mRect!![rectLeft, rectTop, rectRight] = rectBottom
        canvas.rotate(if (startAngle == 360) mRotation else 0F, mRect!!.centerX(), mRect!!.centerY())
        //        canvas.restore();
        mPaint!!.color = strokeColor
        mPaint!!.shader = null
        canvas.drawArc(mRect!!, startAngle.toFloat(), sweepAngle.toFloat(), false, mPaint!!)
        mPaint!!.color = mLowColor
        mPaint!!.strokeWidth = strokeWidth
        //        mPaint.setShader(new LinearGradient(getWidth(), getHeight(), 0, 0, mPointEndColor, mPointStartColor, Shader.TileMode.CLAMP));
        mPaint!!.shader = null
        if (pointSize > 0) {
            if (mPoint > startAngle + pointSize / 2) {
                canvas.drawArc(
                    mRect!!,
                    mPoint - pointSize / 2.toFloat(),
                    pointSize.toFloat(),
                    false,
                    mPaint!!
                )
            } else { //to avoid excedding start/zero ~point
                canvas.drawArc(mRect!!, startAngle.toFloat(), mPoint1, false, mPaint!!)
                mPaint!!.color = mMediumColor
                mPaint!!.shader = null
                canvas.drawArc(mRect!!, mPoint2, mPoint3, false, mPaint!!)
                mPaint!!.color = mHighColor
                mPaint!!.shader = null
                canvas.drawArc(mRect!!, mPoint4 + 10, startAngle * 0.20f, false, mPaint!!)

//                if (mDividerSize > 0) {
                mPaint!!.color = mPointerColor
                mPaint!!.shader = null
                mPaint!!.strokeCap = Paint.Cap.ROUND
                mPaint!!.strokeWidth = mPointerSize.toFloat()
                val i = if (isDividerDrawFirst) 0 else 1
                val max = 10
                val cx = getWidth() / 2f
                val cy = getHeight() / 2f
                val angle = Math.toRadians(mPointerAngle).toFloat()
                val scaleMarkSize = resources.displayMetrics.density * 25
                val r = Math.min(getWidth(), getHeight()) / 2.05f
                val startX =
                    (cx + r * Math.sin(angle.toDouble())).toFloat()
                val startY =
                    (cy - r * Math.cos(angle.toDouble())).toFloat()
                val stopX =
                    (cx + (r - scaleMarkSize) * Math.sin(angle.toDouble())).toFloat()
                val stopY =
                    (cy - (r - scaleMarkSize) * Math.cos(angle.toDouble())).toFloat()
                if (mShowPointer) {
                    canvas.drawLine(startX, startY, stopX, stopY, mPaint!!)
                }
            }
        } else {
            if (mValue == startValue) canvas.drawArc(
                mRect!!,
                startAngle.toFloat(),
                DEFAULT_LONG_POINTER_SIZE.toFloat(),
                false,
                mPaint!!
            ) else canvas.drawArc(
                mRect!!,
                startAngle.toFloat(),
                mPoint - startAngle.toFloat(),
                false,
                mPaint!!
            )
        }
        if (mDividerSize > 0) {
            mPaint!!.color = Color.GREEN
            mPaint!!.shader = null
            var i = if (isDividerDrawFirst) 0 else 1
            var max = if (isDividerDrawLast) mDividersCount + 1 else mDividersCount
            max = 5
            while (i < max) {
                canvas.drawArc(
                    mRect!!,
                    startAngle + 50.toFloat(),
                    mDividerSize.toFloat(),
                    false,
                    mPaint!!
                )
                i++
            }
        }


//        canvas.restore();
    }

    var value: Int
        get() = mValue
        set(value) {
            mValue = value
            mPoint = (startAngle + (mValue - startValue) * mPointAngle).toInt()
            invalidate()
        }

    var strokeCap: String?
        get() = mStrokeCap
        set(strokeCap) {
            mStrokeCap = strokeCap
            if (mPaint != null) {
                if (mStrokeCap == "BUTT") {
                    mPaint!!.strokeCap = Paint.Cap.BUTT
                } else if (mStrokeCap == "ROUND") {
                    mPaint!!.strokeCap = Paint.Cap.ROUND
                }
            }
        }

    var endValue: Int
        get() = mEndValue
        set(endValue) {
            mEndValue = endValue
            mPointAngle = Math.abs(sweepAngle).toDouble() / (mEndValue - startValue)
            invalidate()
        }

    fun setDividerStep(dividerStep: Int) {
        if (dividerStep > 0) {
            mDividersCount = 100 / dividerStep
            mDividerStepAngle = sweepAngle / mDividersCount
        }
    }

    fun setDividerSize(dividerSize: Int) {
        if (dividerSize > 0) {
            mDividerSize = sweepAngle / (Math.abs(mEndValue - startValue) / dividerSize)
        }
    }

    companion object {
        private const val TAG = "Indicator"
        private const val DEFAULT_LONG_POINTER_SIZE = 1
    }
}