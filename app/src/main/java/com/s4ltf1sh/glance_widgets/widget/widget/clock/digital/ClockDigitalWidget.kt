package com.s4ltf1sh.glance_widgets.widget.widget.clock.digital

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.ContentScale
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.s4ltf1sh.glance_widgets.MainActivity
import com.s4ltf1sh.glance_widgets.model.Widget
import com.s4ltf1sh.glance_widgets.model.WidgetSize
import com.s4ltf1sh.glance_widgets.model.WidgetType
import com.s4ltf1sh.glance_widgets.model.clock.digital.WidgetClockDigitalData
import com.s4ltf1sh.glance_widgets.widget.core.BaseAppWidget
import com.s4ltf1sh.glance_widgets.widget.widget.quotes.getImageProvider
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

@Composable
fun ClockDigitalWidget(
    widget: Widget,
    widgetId: Int
) {
    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .cornerRadius(16.dp)
            .clickable(
                actionStartActivity<MainActivity>(
                    parameters = actionParametersOf(
                        BaseAppWidget.KEY_WIDGET_ID to widgetId,
                        BaseAppWidget.KEY_WIDGET_TYPE to widget.type.typeId,
                        BaseAppWidget.KEY_WIDGET_SIZE to widget.size.name
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        if (widget.data.isNotEmpty()) {
            val clockData = moshi.adapter(WidgetClockDigitalData::class.java).fromJson(widget.data)
                ?: throw Exception("Invalid clock digital data")

            // Background image
            Image(
                provider = getImageProvider(clockData.backgroundPath),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = GlanceModifier.fillMaxSize()
            )

            // Overlay with clock information
            when (widget.type) {
                is WidgetType.Clock.Digital.Type1 -> ClockDigitalType1Layout(widget.size)
                is WidgetType.Clock.Digital.Type2 -> ClockDigitalType2Layout(widget.size)
                else -> ClockDigitalEmptyState()
            }
        } else {
            ClockDigitalEmptyState()
        }
    }
}

@Composable
private fun ClockDigitalType1Layout(
    widgetSize: WidgetSize
) {
    val timeTextSize = when (widgetSize) {
        WidgetSize.SMALL -> 36F
        WidgetSize.MEDIUM -> 60F
        WidgetSize.LARGE -> 96F
    }

    val dateTextSize = when (widgetSize) {
        WidgetSize.SMALL -> 16F
        WidgetSize.MEDIUM -> 20F
        WidgetSize.LARGE -> 32F
    }

    val dayTextSize = when (widgetSize) {
        WidgetSize.SMALL -> 24F
        WidgetSize.MEDIUM -> 32F
        WidgetSize.LARGE -> 48F
    }

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        ClockDigitalType1(
            modifier = GlanceModifier.fillMaxSize(),
            timeTextSize = timeTextSize,
            dateTextSize = dateTextSize,
            dayTextSize = dayTextSize
        )
    }
}

@Composable
private fun ClockDigitalType2Layout(
    widgetSize: WidgetSize
) {
    val timeTextSize = when (widgetSize) {
        WidgetSize.SMALL -> 60F
        WidgetSize.MEDIUM -> 60F
        WidgetSize.LARGE -> 100F
    }

    val dateTextSize = when (widgetSize) {
        WidgetSize.SMALL -> 16F
        WidgetSize.MEDIUM -> 16F
        WidgetSize.LARGE -> 36F
    }

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        ClockDigitalType2(
            modifier = GlanceModifier.fillMaxSize(),
            timeTextSize = timeTextSize,
            dateTextSize = dateTextSize
        )
    }
}

@Composable
private fun ClockDigitalEmptyState() {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Tap to configure clock",
            style = TextStyle(
                color = ColorProvider(Color.White),
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        )
    }
}

@Composable
private fun ClockDigitalErrorState() {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Unable to load clock",
            style = TextStyle(
                color = ColorProvider(Color.White),
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        )
    }
}