package com.s4ltf1sh.glance_widgets.widget_core.small

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.s4ltf1sh.glance_widgets.widget_core.medium.WidgetMedium

class WidgetSmallReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WidgetSmall()
}