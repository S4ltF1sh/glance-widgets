package com.s4ltf1sh.glance_widgets.ui.customview.clock.digital

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import com.s4ltf1sh.glance_widgets.ui.customview.clock.BaseDigitalClockView

/**
 * Minimal clock widget - No shadow, simple design
 */
class MinimalClockWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseDigitalClockView(context, attrs, defStyleAttr) {
    
    init {
        // No shadow for minimal look
        enableShadow = false
        
        // Simple colors
        timeTextColor = Color.BLACK
        dateTextColor = Color.GRAY
        
        // Format
        format12Hour = "h:mm a"
        format24Hour = "HH:mm"
        dateFormat = "EEE, MMM d"
        
        // Text alignment
        textAlign = Paint.Align.CENTER
        textSpacing = 4f
        
        // Thin typeface
        timeTypeface = Typeface.create("sans-serif-thin", Typeface.NORMAL)
        dateTypeface = Typeface.create("sans-serif-thin", Typeface.NORMAL)
        
        // Default sizes
        timeTextSize = 48f
        dateTextSize = 16f
    }
}