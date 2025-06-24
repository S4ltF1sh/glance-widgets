package com.s4ltf1sh.glance_widgets.db.calendar

import kotlinx.serialization.Serializable

@Serializable
data class WidgetCalendarData(
    val year: Int,
    val month: Int, // 1-12
    val selectedDay: Int? = null,
    val todayDay: Int? = null,
    val backgroundPath: String? = null,
)