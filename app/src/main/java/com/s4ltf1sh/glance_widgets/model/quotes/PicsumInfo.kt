package com.s4ltf1sh.glance_widgets.model.quotes

import kotlinx.serialization.Serializable

@Serializable
data class PicsumInfo(
    val id: String,
    val author: String,
    val width: Int,
    val height: Int,
    val url: String,
    val download_url: String,
)