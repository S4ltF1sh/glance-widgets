package com.s4ltf1sh.glance_widgets.widget_core.large

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class WidgetLargeReceiver: GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WidgetLarge()
}