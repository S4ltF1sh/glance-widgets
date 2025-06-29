package com.s4ltf1sh.glance_widgets.db.calendar

import kotlinx.serialization.Serializable

@Serializable
data class WidgetCalendarData(
    val backgroundPath: String? = null,
    val lastedUpdate: Long = System.currentTimeMillis()
) {
    companion object {
        fun Init(): WidgetCalendarData {
            return WidgetCalendarData(backgroundPath = null)
        }
    }
}