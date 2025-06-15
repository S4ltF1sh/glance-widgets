package com.s4ltf1sh.glance_widgets.model.quotes

import kotlinx.serialization.Serializable

@Serializable
sealed interface QuoteWidgetState {
    @Serializable
    data object Loading : QuoteWidgetState

    @Serializable
    data class Available(
        val imageUrl: String,
        val imageAuthor: String? = null,
        val downloadedImageFilePath: String? = null,
    ) : QuoteWidgetState

    @Serializable
    data class Unavailable(val message: String) : QuoteWidgetState
}