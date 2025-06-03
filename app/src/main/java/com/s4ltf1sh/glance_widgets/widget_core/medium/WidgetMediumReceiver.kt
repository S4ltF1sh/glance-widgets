package com.s4ltf1sh.glance_widgets.widget_core.medium

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class WidgetMediumReceiver: GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WidgetMedium()
}