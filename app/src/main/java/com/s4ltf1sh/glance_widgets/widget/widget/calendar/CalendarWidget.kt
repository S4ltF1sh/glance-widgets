package com.s4ltf1sh.glance_widgets.widget.widget.calendar

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.background
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import com.s4ltf1sh.glance_widgets.MainActivity
import com.s4ltf1sh.glance_widgets.model.WidgetSize
import androidx.glance.layout.Column
import androidx.glance.layout.Alignment
import com.s4ltf1sh.glance_widgets.widget.core.BaseAppWidget
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.s4ltf1sh.glance_widgets.model.Widget

@SuppressLint("RestrictedApi")
@Composable
fun CalendarWidget(widget: Widget, widgetId: Int) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(Color.Green)
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "📅",
                style = TextStyle(fontSize = 24.sp)
            )
            Text(
                text = "Calendar",
                style = TextStyle(color = ColorProvider(Color.White))
            )
            when (widget.size) {
                WidgetSize.SMALL -> Text("Jun 4")
                WidgetSize.MEDIUM -> {
                    Text("June 4, 2025")
                    Text("Wednesday")
                }

                WidgetSize.LARGE -> {
                    Text("June 4, 2025")
                    Text("Wednesday")
                    Text("2 events today")
                    Text("Next: Meeting at 2 PM")
                }
            }
        }
    }
}