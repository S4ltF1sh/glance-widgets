package com.s4ltf1sh.glance_widgets.widget.core.large

import androidx.glance.appwidget.GlanceAppWidget
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetSize
import com.s4ltf1sh.glance_widgets.widget.core.BaseWidgetReceiver

class WidgetLargeReceiver : BaseWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WidgetLarge()
    override val glanceWidgetSize: GlanceWidgetSize
        get() = GlanceWidgetSize.LARGE
}