package com.s4ltf1sh.glance_widgets.model

import kotlinx.serialization.Serializable

@Serializable
data class GlanceWidget(
    val widgetId: Int,
    val type: GlanceWidgetType,
    val size: GlanceWidgetSize,
    val lastUpdated: Long = System.currentTimeMillis(),
    val data: String = "" // JSON data specific to widget type
) {
    companion object {
        val EMPTY = GlanceWidget(
            widgetId = -1,
            type = GlanceWidgetType.None,
            size = GlanceWidgetSize.SMALL
        )
    }
}
