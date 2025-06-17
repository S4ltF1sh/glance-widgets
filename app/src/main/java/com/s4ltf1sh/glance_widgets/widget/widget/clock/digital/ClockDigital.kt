package com.s4ltf1sh.glance_widgets.widget.widget.clock.digital

import android.util.TypedValue
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.appwidget.AndroidRemoteViews
import com.s4ltf1sh.glance_widgets.R

@Composable
fun ClockDigital(
    timeFormat: String,
    timeTextSize: Float = 42F,
    timeTextColor: Int,
    modifier: GlanceModifier
) {
    val packageName = LocalContext.current.packageName
    val clockId = R.id.clock_digital
    val remoteViews = RemoteViews(packageName, R.layout.component_text_clock).apply {
        setCharSequence(clockId, "setFormat24Hour", timeFormat)
        setCharSequence(clockId, "setFormat12Hour", timeFormat)
        setTextViewTextSize(clockId, TypedValue.COMPLEX_UNIT_SP, timeTextSize)
        setTextColor(clockId, timeTextColor)
    }



    AndroidRemoteViews(
        remoteViews = remoteViews,
        modifier = modifier
    )
}