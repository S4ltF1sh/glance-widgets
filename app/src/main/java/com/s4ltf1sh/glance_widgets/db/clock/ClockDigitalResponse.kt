package com.s4ltf1sh.glance_widgets.db.clock

data class ClockDigitalResponse(
    val id: Int,
    val backgroundUrl: String,
    val type: String // 1 or 2 (base on design)
)