package com.s4ltf1sh.glance_widgets.widget.core.medium

import androidx.glance.appwidget.GlanceAppWidget
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetSize
import com.s4ltf1sh.glance_widgets.widget.core.BaseWidgetReceiver

class WidgetMediumReceiver: BaseWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WidgetMedium()
    override val glanceWidgetSize: GlanceWidgetSize
        get() = GlanceWidgetSize.MEDIUM
}