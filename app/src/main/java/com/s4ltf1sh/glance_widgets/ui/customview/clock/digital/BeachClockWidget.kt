package com.s4ltf1sh.glance_widgets.ui.customview.clock.digital

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import com.s4ltf1sh.glance_widgets.ui.customview.clock.BaseDigitalClockView

/**
 * Beach/Ocean themed clock widget - Style 01
 * White text with shadow on transparent background
 */
open class BeachClockWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseDigitalClockView(context, attrs, defStyleAttr) {
    
    init {
        // Text styling
        timeTextColor = Color.WHITE
        dateTextColor = Color.WHITE
        
        // Shadow for better readability
        enableShadow = true
        shadowRadius = 8f
        shadowDx = 0f
        shadowDy = 4f
        shadowColor = Color.argb(180, 0, 0, 0)
        
        // Format
        format12Hour = "h:mm"
        format24Hour = "HH:mm"
        dateFormat = "EEEE"  // Just day name
        
        // Text alignment
        textAlign = Paint.Align.CENTER
        textSpacing = 5f
        
        // Set typefaces
        timeTypeface = Typeface.create("sans-serif-medium", Typeface.BOLD)
        dateTypeface = Typeface.create("sans-serif-light", Typeface.NORMAL)
    }
    
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        
        // Adjust text sizes based on widget size
        when {
            w <= 200 -> {  // Small widget
                timeTextSize = 48f
                dateTextSize = 16f
                textSpacing = 2f
            }
            w <= 400 -> {  // Medium widget
                timeTextSize = 72f
                dateTextSize = 20f
                textSpacing = 5f
            }
            else -> {      // Large widget
                timeTextSize = 96f
                dateTextSize = 28f
                textSpacing = 8f
            }
        }
    }
}

/**
 * Beach clock with full date - Style 01 variant
 */
class BeachClockFullDateWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BeachClockWidget(context, attrs, defStyleAttr) {
    
    init {
        // Show full date
        dateFormat = "EEEE, MMMM d"
    }
}