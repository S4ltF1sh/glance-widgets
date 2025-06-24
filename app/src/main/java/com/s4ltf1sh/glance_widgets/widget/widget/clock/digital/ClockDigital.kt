package com.s4ltf1sh.glance_widgets.widget.widget.clock.digital

import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.appwidget.AndroidRemoteViews
import com.s4ltf1sh.glance_widgets.R

@Composable
fun ClockDigitalType2(
    timeFormat12Hour: String = "h:mm",
    timeFormat24Hour: String = "HH:mm",
    dateFormat: String = "EEEE, MMMM d",
    timeTextSize: Float = 48F,
    dateTextSize: Float = 16F,
    timeTextColor: Int = Color.WHITE,
    dateTextColor: Int = Color.WHITE,
    gravity: Int = Gravity.CENTER,
    modifier: GlanceModifier
) {
    val packageName = LocalContext.current.packageName

    val remoteViews = RemoteViews(packageName, R.layout.component_clock_digital_2).apply {
        // Configure time TextClock
        val timeClockId = R.id.clock_time
        setCharSequence(timeClockId, "setFormat12Hour", timeFormat12Hour)
        setCharSequence(timeClockId, "setFormat24Hour", timeFormat24Hour)
        setTextViewTextSize(timeClockId, TypedValue.COMPLEX_UNIT_SP, timeTextSize)
        setTextColor(timeClockId, timeTextColor)

        // Configure date TextClock
        val dateClockId = R.id.clock_date
        setCharSequence(dateClockId, "setFormat12Hour", dateFormat)
        setCharSequence(dateClockId, "setFormat24Hour", dateFormat)
        setTextViewTextSize(dateClockId, TypedValue.COMPLEX_UNIT_SP, dateTextSize)
        setTextColor(dateClockId, dateTextColor)

        // Set gravity for container
        setInt(R.id.clock_container, "setGravity", gravity)
    }

    AndroidRemoteViews(
        remoteViews = remoteViews,
        modifier = modifier
    )
}

@Composable
fun ClockDigitalType1(
    dayFormat: String = "EEEE",
    dateFormat: String = "MMMM d",
    timeFormat12Hour: String = "h:mm",
    timeFormat24Hour: String = "HH:mm",
    dayTextSize: Float = 64F,
    dateTextSize: Float = 40F,
    timeTextSize: Float = 120F,
    textColor: Int = Color.WHITE,
    gravity: Int = Gravity.CENTER,
    modifier: GlanceModifier
) {
    val packageName = LocalContext.current.packageName

    val remoteViews = RemoteViews(packageName, R.layout.component_clock_digital_1).apply {
        // Configure day TextClock
        val dayClockId = R.id.clock_day
        setCharSequence(dayClockId, "setFormat12Hour", dayFormat)
        setCharSequence(dayClockId, "setFormat24Hour", dayFormat)
        setTextViewTextSize(dayClockId, TypedValue.COMPLEX_UNIT_SP, dayTextSize)
        setTextColor(dayClockId, textColor)

        // Configure date TextClock
        val dateClockId = R.id.clock_date
        setCharSequence(dateClockId, "setFormat12Hour", dateFormat)
        setCharSequence(dateClockId, "setFormat24Hour", dateFormat)
        setTextViewTextSize(dateClockId, TypedValue.COMPLEX_UNIT_SP, dateTextSize)
        setTextColor(dateClockId, textColor)

        // Configure time TextClock
        val timeClockId = R.id.clock_time
        setCharSequence(timeClockId, "setFormat12Hour", timeFormat12Hour)
        setCharSequence(timeClockId, "setFormat24Hour", timeFormat24Hour)
        setTextViewTextSize(timeClockId, TypedValue.COMPLEX_UNIT_SP, timeTextSize)
        setTextColor(timeClockId, textColor)

        // Set gravity for container
        setInt(R.id.clock_container, "setGravity", gravity)
    }

    AndroidRemoteViews(
        remoteViews = remoteViews,
        modifier = modifier
    )
}