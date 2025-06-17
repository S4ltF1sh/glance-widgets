package com.s4ltf1sh.glance_widgets.db.weather

data class WeatherResponse(
    val id: Int,
    val backgroundUrl: String, // URL of the weather background image
    val type: String // 1 -> 4, indicating the type of weather widget (base on design)
)