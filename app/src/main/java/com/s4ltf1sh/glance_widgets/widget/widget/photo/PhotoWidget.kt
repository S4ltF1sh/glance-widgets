package com.s4ltf1sh.glance_widgets.widget.widget.photo

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
import com.s4ltf1sh.glance_widgets.widget.model.WidgetSize
import com.s4ltf1sh.glance_widgets.widget.core.BaseAppWidget

@SuppressLint("RestrictedApi")
@Composable
fun PhotoWidget(widget: WidgetEntity, widgetId: Int) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(Color.Magenta)
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
                text = "🖼️",
                style = TextStyle(fontSize = 24.sp)
            )
            Text(
                text = "Photo",
                style = TextStyle(color = ColorProvider(Color.White))
            )
            when (widget.size) {
                WidgetSize.SMALL -> Text("Daily Photo")
                WidgetSize.MEDIUM -> {
                    Text("Photo of the Day")
                    Text("Nature Collection")
                }

                WidgetSize.LARGE -> {
                    Text("Photo of the Day")
                    Text("Nature Collection")
                    Text("Mountain Sunset")
                    Text("Tap to view full")
                }
            }
        }
    }
}