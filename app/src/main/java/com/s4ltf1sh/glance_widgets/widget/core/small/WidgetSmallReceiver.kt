package com.s4ltf1sh.glance_widgets.widget.core.small

import androidx.glance.appwidget.GlanceAppWidget
import com.s4ltf1sh.glance_widgets.widget.core.BaseWidgetReceiver
import com.s4ltf1sh.glance_widgets.model.WidgetSize

class WidgetSmallReceiver: BaseWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WidgetSmall()
    override val widgetSize: WidgetSize
        get() = WidgetSize.SMALL
}