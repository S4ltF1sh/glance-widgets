package com.s4ltf1sh.glance_widgets.db.calendar

data class CalendarResponse(
    val id: Int,
    val backgroundUrl: String, // URL of the calendar image
    val type: String // 1 -> 4, indicating the type of calendar (base on design)
)