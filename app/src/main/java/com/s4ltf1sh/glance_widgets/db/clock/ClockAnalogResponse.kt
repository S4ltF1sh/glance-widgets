package com.s4ltf1sh.glance_widgets.db.clock

data class ClockAnalogResponse(
    val id: Int,
    val backgroundUrl: String, // URL of the clock background image
    val dialBackgroundUrl: String, // URL of the clock dial background image
    val hourHandUrl: String, // URL of the hour hand image
    val minuteHandUrl: String, // URL of the minute hand image
    val secondHandUrl: String?, // URL of the second hand image (if available, can be null)
)