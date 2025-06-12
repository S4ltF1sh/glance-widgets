package com.s4ltf1sh.glance_widgets.widget.core

import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.s4ltf1sh.glance_widgets.model.WidgetSize

abstract class BaseWidgetReceiver: GlanceAppWidgetReceiver() {
    abstract val widgetSize: WidgetSize
}