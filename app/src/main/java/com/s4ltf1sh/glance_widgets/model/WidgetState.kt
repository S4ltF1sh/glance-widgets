package com.s4ltf1sh.glance_widgets.model

import kotlinx.serialization.Serializable

@Serializable
sealed interface WidgetState {
    @Serializable
    data object Initial : WidgetState

    @Serializable
    data object Loading : WidgetState

    @Serializable
    data object Success : WidgetState

    @Serializable
    data object Error : WidgetState
}