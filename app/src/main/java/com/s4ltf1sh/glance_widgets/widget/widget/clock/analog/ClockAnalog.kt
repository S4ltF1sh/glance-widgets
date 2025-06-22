package com.s4ltf1sh.glance_widgets.widget.widget.clock.analog

import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.appwidget.AndroidRemoteViews
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import com.s4ltf1sh.glance_widgets.R
import com.s4ltf1sh.glance_widgets.model.WidgetType
import com.s4ltf1sh.glance_widgets.model.clock.analog.WidgetClockAnalogData

@Composable
fun ClockAnalog(
    paddingVertical: Dp,
    data: WidgetClockAnalogData,
    widgetType: WidgetType
) {
    val fileDirs = LocalContext.current.filesDir.absolutePath + "/.widget_images/"
    val packageName = LocalContext.current.packageName
    val remoteViews = RemoteViews(
        packageName,
        when (widgetType) {
            WidgetType.Clock.Analog.Type1 -> R.layout.component_clock_analog_1
            else -> R.layout.component_clock_analog_2 // Default to Type2 if not specified
        }
    ).apply {
//        setIcon(
//            R.id.clock_analog,
//            "setDial",
//            Icon.createWithBitmap(getBitmapFromPath(data.dialBackgroundPath))
//        )
//
//        setIcon(
//            R.id.clock_analog,
//            "setMinuteHand",
//            Icon.createWithBitmap(getBitmapFromPath(data.minuteHandPath))
//        )
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            data.secondHandPath?.let {
//                setIcon(
//                    R.id.clock_analog,
//                    "setSecondHand",
//                    Icon.createWithBitmap(getBitmapFromPath(it))
//                )
//            }
//        }
//
//        setIcon(
//            R.id.clock_analog,
//            "setHourHand",
//            Icon.createWithBitmap(getBitmapFromPath(data.hourHandPath))
//        )
    }

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(paddingVertical),
        contentAlignment = Alignment.Center
    ) {

        AndroidRemoteViews(remoteViews = remoteViews)
    }
}