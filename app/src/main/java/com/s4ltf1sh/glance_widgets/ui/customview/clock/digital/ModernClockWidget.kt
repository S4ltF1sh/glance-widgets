package com.s4ltf1sh.glance_widgets.ui.customview.clock.digital

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import com.s4ltf1sh.glance_widgets.ui.customview.clock.BaseDigitalClockView

/**
 * Bold modern clock widget
 */
class ModernClockWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseDigitalClockView(context, attrs, defStyleAttr) {
    
    init {
        // Bold white text
        timeTextColor = Color.WHITE
        dateTextColor = Color.argb(230, 255, 255, 255)
        
        // Strong shadow for depth
        enableShadow = true
        shadowRadius = 12f
        shadowDx = 0f
        shadowDy = 6f
        shadowColor = Color.argb(200, 0, 0, 0)
        
        // Format
        format12Hour = "h:mm"
        format24Hour = "HH:mm"
        dateFormat = "EEEE"
        
        // Text alignment
        textAlign = Paint.Align.CENTER
        textSpacing = 0f  // Tight spacing
        
        // Bold typeface
        timeTypeface = Typeface.create("sans-serif-black", Typeface.BOLD)
        dateTypeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
        
        // Large default sizes
        timeTextSize = 100f
        dateTextSize = 24f
    }
}