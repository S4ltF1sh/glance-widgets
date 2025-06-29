package com.s4ltf1sh.glance_widgets.widget.core.small

import androidx.glance.appwidget.GlanceAppWidget
import com.s4ltf1sh.glance_widgets.widget.core.BaseWidgetReceiver
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetSize

class WidgetSmallReceiver: BaseWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WidgetSmall()
    override val glanceWidgetSize: GlanceWidgetSize
        get() = GlanceWidgetSize.SMALL
}