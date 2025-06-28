package com.s4ltf1sh.glance_widgets.widget.widget.clock.analog

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
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.s4ltf1sh.glance_widgets.MainActivity
import com.s4ltf1sh.glance_widgets.model.GlanceWidget
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetSize
import com.s4ltf1sh.glance_widgets.model.clock.analog.WidgetClockAnalogData
import com.s4ltf1sh.glance_widgets.widget.core.BaseAppWidget
import com.s4ltf1sh.glance_widgets.widget.widget.quotes.getImageProvider
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

@Composable
fun ClockAnalogWidget(
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
            val clockData = moshi.adapter(WidgetClockAnalogData::class.java).fromJson(glanceWidget.data)
                ?: throw Exception("Invalid clock analog data")

            // Background image
            Image(
                provider = getImageProvider(clockData.backgroundPath),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = GlanceModifier.fillMaxSize()
            )

            ClockAnalogLayout(glanceWidget, clockData)
        } else {
            ClockAnalogEmptyState()
        }
    }
}

@Composable
private fun ClockAnalogLayout(glanceWidget: GlanceWidget, data: WidgetClockAnalogData) {
    val paddingVertical = when (glanceWidget.size) {
        GlanceWidgetSize.SMALL -> 18.dp
        GlanceWidgetSize.MEDIUM -> 18.dp
        GlanceWidgetSize.LARGE -> 16.dp
    }

    ClockAnalog(
        paddingVertical = paddingVertical,
        data = data,
        glanceWidgetType = glanceWidget.type
    )
}

@Composable
private fun ClockAnalogEmptyState() {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Tap to configure analog clock",
            style = TextStyle(
                color = ColorProvider(Color.Black),
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        )
    }
}