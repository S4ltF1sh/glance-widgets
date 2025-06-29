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
import com.s4ltf1sh.glance_widgets.model.GlanceWidget
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetSize
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetType
import com.s4ltf1sh.glance_widgets.model.clock.digital.WidgetClockDigitalData
import com.s4ltf1sh.glance_widgets.widget.core.BaseAppWidget
import com.s4ltf1sh.glance_widgets.widget.widget.quotes.getImageProvider
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

@Composable
fun ClockDigitalWidget(
    glanceWidget: GlanceWidget,
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
                        BaseAppWidget.KEY_WIDGET_TYPE to glanceWidget.type.typeId,
                        BaseAppWidget.KEY_WIDGET_SIZE to glanceWidget.size.name
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        if (glanceWidget.data.isNotEmpty()) {
            val clockData = moshi.adapter(WidgetClockDigitalData::class.java).fromJson(glanceWidget.data)
                ?: throw Exception("Invalid clock digital data")

            // Background image
            Image(
                provider = getImageProvider(clockData.backgroundPath),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = GlanceModifier.fillMaxSize()
            )

            // Overlay with clock information
            when (glanceWidget.type) {
                is GlanceWidgetType.Clock.Digital.Type1Glance -> ClockDigitalType1Layout(glanceWidget.size)
                is GlanceWidgetType.Clock.Digital.Type2Glance -> ClockDigitalType2Layout(glanceWidget.size)
                else -> ClockDigitalEmptyState()
            }
        } else {
            ClockDigitalEmptyState()
        }
    }
}

@Composable
private fun ClockDigitalType1Layout(
    glanceWidgetSize: GlanceWidgetSize
) {
    val timeTextSize = when (glanceWidgetSize) {
        GlanceWidgetSize.SMALL -> 36F
        GlanceWidgetSize.MEDIUM -> 60F
        GlanceWidgetSize.LARGE -> 96F
    }

    val dateTextSize = when (glanceWidgetSize) {
        GlanceWidgetSize.SMALL -> 16F
        GlanceWidgetSize.MEDIUM -> 20F
        GlanceWidgetSize.LARGE -> 32F
    }

    val dayTextSize = when (glanceWidgetSize) {
        GlanceWidgetSize.SMALL -> 24F
        GlanceWidgetSize.MEDIUM -> 32F
        GlanceWidgetSize.LARGE -> 48F
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
    glanceWidgetSize: GlanceWidgetSize
) {
    val timeTextSize = when (glanceWidgetSize) {
        GlanceWidgetSize.SMALL -> 60F
        GlanceWidgetSize.MEDIUM -> 60F
        GlanceWidgetSize.LARGE -> 100F
    }

    val dateTextSize = when (glanceWidgetSize) {
        GlanceWidgetSize.SMALL -> 16F
        GlanceWidgetSize.MEDIUM -> 16F
        GlanceWidgetSize.LARGE -> 36F
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