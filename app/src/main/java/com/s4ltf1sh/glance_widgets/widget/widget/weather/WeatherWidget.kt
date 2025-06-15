package com.s4ltf1sh.glance_widgets.widget.widget.weather

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.s4ltf1sh.glance_widgets.MainActivity
import com.s4ltf1sh.glance_widgets.db.WidgetEntity
import com.s4ltf1sh.glance_widgets.model.Widget
import com.s4ltf1sh.glance_widgets.model.WidgetSize
import com.s4ltf1sh.glance_widgets.widget.core.BaseAppWidget

@SuppressLint("RestrictedApi")
@Composable
fun WeatherWidget(widget: Widget, widgetId: Int) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(Color.Blue)
            .clickable(
                actionStartActivity<MainActivity>(
                    parameters = actionParametersOf(
                        BaseAppWidget.KEY_WIDGET_ID to widgetId,
                        BaseAppWidget.KEY_WIDGET_TYPE to widget.type.name,
                        BaseAppWidget.KEY_WIDGET_SIZE to widget.size.name
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🌤️",
                style = TextStyle(fontSize = 24.sp)
            )
            Text(
                text = "Weather",
                style = TextStyle(color = ColorProvider(Color.White))
            )
            when (widget.size) {
                WidgetSize.SMALL -> Text("25°C")
                WidgetSize.MEDIUM -> {
                    Text("25°C - Sunny")
                    Text("Hanoi")
                }

                WidgetSize.LARGE -> {
                    Text("25°C - Sunny")
                    Text("Hanoi, Vietnam")
                    Text("Humidity: 65%")
                    Text("Wind: 10 km/h")
                }
            }
        }
    }
}