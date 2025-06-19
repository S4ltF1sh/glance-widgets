package com.s4ltf1sh.glance_widgets.ui.customview.clock.digital

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.core.graphics.toColorInt
import com.s4ltf1sh.glance_widgets.ui.customview.clock.BaseDigitalClockView

/**
 * Cozy/Indoor themed clock widget - Style 02
 * Warm white text with softer shadows
 */
class CozyClockWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseDigitalClockView(context, attrs, defStyleAttr) {
    
    init {
        // Warm white colors
        timeTextColor = "#FFFAF0".toColorInt()  // Floral white
        dateTextColor = "#FFF5E6".toColorInt()  // Slightly warmer
        
        // Softer shadow
        enableShadow = true
        shadowRadius = 6f
        shadowDx = 0f
        shadowDy = 3f
        shadowColor = Color.argb(120, 0, 0, 0)
        
        // Format
        format12Hour = "h:mm"
        format24Hour = "HH:mm"
        dateFormat = "EEEE, MMMM d"
        
        // Text alignment
        textAlign = Paint.Align.CENTER
        textSpacing = 8f
        
        // Lighter typeface for cozy feel
        timeTypeface = Typeface.create("sans-serif-light", Typeface.NORMAL)
        dateTypeface = Typeface.create("sans-serif-thin", Typeface.NORMAL)
    }
    
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        
        when {
            w <= 200 -> {  // Small widget
                timeTextSize = 42f
                dateTextSize = 14f
                textSpacing = 4f
            }
            w <= 400 -> {  // Medium widget
                timeTextSize = 64f
                dateTextSize = 18f
                textSpacing = 6f
            }
            else -> {      // Large widget
                timeTextSize = 84f
                dateTextSize = 24f
                textSpacing = 10f
            }
        }
    }
}
