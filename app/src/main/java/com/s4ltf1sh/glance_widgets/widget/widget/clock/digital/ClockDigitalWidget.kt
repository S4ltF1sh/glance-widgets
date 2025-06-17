package com.s4ltf1sh.glance_widgets.widget.widget.clock.digital

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
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
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.s4ltf1sh.glance_widgets.MainActivity
import com.s4ltf1sh.glance_widgets.model.Widget
import com.s4ltf1sh.glance_widgets.model.WidgetType
import com.s4ltf1sh.glance_widgets.model.clock.digital.WidgetClockDigitalData
import com.s4ltf1sh.glance_widgets.widget.core.BaseAppWidget
import com.s4ltf1sh.glance_widgets.widget.widget.quotes.getImageProvider
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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
                is WidgetType.Clock.Digital.Type1 -> ClockDigitalType1Layout()
                is WidgetType.Clock.Digital.Type2 -> ClockDigitalType2Layout()
                else -> ClockDigitalEmptyState()
            }
        } else {
            ClockDigitalEmptyState()
        }
    }
}

@Preview
@Composable
private fun ClockDigitalType1Layout() {
    // Type1: Day of week => Day and Month => Time (Column layout)
    val calendar = Calendar.getInstance()
    val dayOfWeek = SimpleDateFormat("EEEE", Locale.getDefault()).format(calendar.time)
    val dayAndMonth = SimpleDateFormat("MMMM d", Locale.getDefault()).format(calendar.time)
    val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Day of week
            Text(
                text = dayOfWeek,
                style = TextStyle(
                    color = ColorProvider(Color.White),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
            )
            
            Spacer(modifier = GlanceModifier.height(4.dp))
            
            // Day and Month
            Text(
                text = dayAndMonth,
                style = TextStyle(
                    color = ColorProvider(Color.White),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
            )
            
            Spacer(modifier = GlanceModifier.height(8.dp))
            
            // Time (largest)
            Text(
                text = time,
                style = TextStyle(
                    color = ColorProvider(Color.White),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}

@Preview
@Composable
private fun ClockDigitalType2Layout() {
    // Type2: Time => Day of week, Day and Month (Time first, then info on same line)
    val calendar = Calendar.getInstance()
    val dayOfWeek = SimpleDateFormat("EEEE", Locale.getDefault()).format(calendar.time)
    val dayAndMonth = SimpleDateFormat("MMMM d", Locale.getDefault()).format(calendar.time)
    val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Time (largest, displayed first)
            Text(
                text = time,
                style = TextStyle(
                    color = ColorProvider(Color.White),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            )
            
            Spacer(modifier = GlanceModifier.height(8.dp))
            
            // Day of week and date on same line
            Text(
                text = "$dayOfWeek, $dayAndMonth",
                style = TextStyle(
                    color = ColorProvider(Color.White),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
            )
        }
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