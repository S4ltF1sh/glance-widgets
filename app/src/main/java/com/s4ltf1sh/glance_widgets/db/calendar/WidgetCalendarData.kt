package com.s4ltf1sh.glance_widgets.db.calendar

import kotlinx.serialization.Serializable

@Serializable
data class WidgetCalendarData(
    val currentMonth: Long, // Timestamp of the current month start
    val backgroundPath: String? = null,
    val dayOfWeekDisplayType: Int = 3 //Example with Sun: 1 = S, 3 = Sun
) {
    companion object {
        fun Init(): WidgetCalendarData {
            return WidgetCalendarData(
                currentMonth = System.currentTimeMillis(),
                backgroundPath = null,
                dayOfWeekDisplayType = 3 // Default to Sunday (3 = Sun, 1 = S, etc. based on your requirement
            )
        }
    }
}