package com.s4ltf1sh.glance_widgets.widget.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.glance.GlanceModifier
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.text.Text
import com.s4ltf1sh.glance_widgets.MainActivity
import com.s4ltf1sh.glance_widgets.model.GlanceWidget
import com.s4ltf1sh.glance_widgets.widget.core.BaseAppWidget

@Composable
fun WidgetEmpty(glanceWidget: GlanceWidget, widgetId: Int) {
    Box(
        modifier = GlanceModifier.fillMaxSize().background(Color.White).clickable(
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
        Text(text = "Click to select widget!")
    }
}