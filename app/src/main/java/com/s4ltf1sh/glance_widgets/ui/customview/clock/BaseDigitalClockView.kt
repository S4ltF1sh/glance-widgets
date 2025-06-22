package com.s4ltf1sh.glance_widgets.ui.customview.clock

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import android.util.AttributeSet
import android.widget.RemoteViews
import com.example.customclock.BaseClockView
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Custom clock view that displays time and date
 * This class handles the drawing of text
 */
@RemoteViews.RemoteView
open class BaseDigitalClockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseClockView(context, attrs, defStyleAttr) {

    // Paint objects for drawing
    protected val timePaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    protected val datePaint = TextPaint(Paint.ANTI_ALIAS_FLAG)

    // Text properties
    var timeTextSize: Float = 80f
        set(value) {
            field = value
            timePaint.textSize = value
            invalidate()
        }

    var dateTextSize: Float = 24f
        set(value) {
            field = value
            datePaint.textSize = value
            invalidate()
        }

    var timeTextColor: Int = Color.WHITE
        set(value) {
            field = value
            timePaint.color = value
            invalidate()
        }

    var dateTextColor: Int = Color.WHITE
        set(value) {
            field = value
            datePaint.color = value
            invalidate()
        }

    // Date format
    var dateFormat: String = "EEEE, MMMM d"
        set(value) {
            field = value
            onTimeChanged()
        }

    // Shadow properties
    var enableShadow: Boolean = true
        set(value) {
            field = value
            updateShadow()
            invalidate()
        }

    var shadowRadius: Float = 4f
        set(value) {
            field = value
            updateShadow()
            invalidate()
        }

    var shadowDx: Float = 0f
        set(value) {
            field = value
            updateShadow()
            invalidate()
        }

    var shadowDy: Float = 2f
        set(value) {
            field = value
            updateShadow()
            invalidate()
        }

    var shadowColor: Int = Color.argb(128, 0, 0, 0)
        set(value) {
            field = value
            updateShadow()
            invalidate()
        }

    // Text alignment
    var textAlign: Paint.Align = Paint.Align.CENTER
        set(value) {
            field = value
            timePaint.textAlign = value
            datePaint.textAlign = value
            invalidate()
        }

    // Vertical spacing between time and date
    var textSpacing: Float = 10f
        set(value) {
            field = value
            invalidate()
        }

    // Current date string
    protected var mDateString: String = ""

    // Custom typefaces
    var timeTypeface: Typeface? = null
        set(value) {
            field = value
            timePaint.typeface = value ?: Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            invalidate()
        }

    var dateTypeface: Typeface? = null
        set(value) {
            field = value
            datePaint.typeface = value ?: Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            invalidate()
        }

    init {
        // Initialize paints
        timePaint.apply {
            color = timeTextColor
            textSize = timeTextSize
            typeface = timeTypeface ?: Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = textAlign
        }

        datePaint.apply {
            color = dateTextColor
            textSize = dateTextSize
            typeface = dateTypeface ?: Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            textAlign = textAlign
        }

        updateShadow()
    }

    private fun updateShadow() {
        if (enableShadow) {
            timePaint.setShadowLayer(shadowRadius, shadowDx, shadowDy, shadowColor)
            datePaint.setShadowLayer(shadowRadius * 0.75f, shadowDx, shadowDy, shadowColor)
        } else {
            timePaint.clearShadowLayer()
            datePaint.clearShadowLayer()
        }
    }

    override fun onTimeChanged() {
        super.onTimeChanged()

        // Update date string
        val dateFormatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        dateFormatter.timeZone = mTime.timeZone
        mDateString = dateFormatter.format(mTime.time)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = when (textAlign) {
            Paint.Align.LEFT -> paddingLeft.toFloat()
            Paint.Align.RIGHT -> width - paddingRight.toFloat()
            else -> width / 2f
        }

        // Calculate vertical center considering both texts
        val totalHeight = timePaint.textSize + dateTextSize + textSpacing
        val centerY = (height - paddingTop - paddingBottom) / 2f + paddingTop

        // Draw date (above time)
        val dateY = centerY - totalHeight / 2f + dateTextSize
        canvas.drawText(mDateString, centerX, dateY, datePaint)

        // Draw time (below date)
        val timeY = dateY + textSpacing + timePaint.textSize * 0.8f
        canvas.drawText(mTimeString, centerX, timeY, timePaint)
    }

    /**
     * Set both text colors at once
     */
    fun setTextColors(color: Int) {
        timeTextColor = color
        dateTextColor = color
    }

    /**
     * Set time text style
     */
    fun setTimeTextStyle(size: Float, color: Int, typeface: Typeface? = null) {
        timeTextSize = size
        timeTextColor = color
        timeTypeface = typeface
    }

    /**
     * Set date text style
     */
    fun setDateTextStyle(size: Float, color: Int, typeface: Typeface? = null) {
        dateTextSize = size
        dateTextColor = color
        dateTypeface = typeface
    }
}