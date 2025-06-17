package com.s4ltf1sh.glance_widgets.model

import kotlinx.serialization.Serializable

@Serializable
data class Widget(
    val widgetId: Int,
    val type: WidgetType,
    val size: WidgetSize,
    val lastUpdated: Long = System.currentTimeMillis(),
    val data: String = "" // JSON data specific to widget type
) {
    companion object {
        val EMPTY = Widget(
            widgetId = -1,
            type = WidgetType.None,
            size = WidgetSize.SMALL
        )
    }
}
