package com.s4ltf1sh.glance_widgets.model.clock.digital

import kotlinx.serialization.Serializable

@Serializable
data class ClockDigitalResponse(
    val id: Int,
    val font_download_url: String,
    val bg_download_url: String,
)
