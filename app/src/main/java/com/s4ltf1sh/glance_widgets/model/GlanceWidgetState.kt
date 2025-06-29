package com.s4ltf1sh.glance_widgets.model

import kotlinx.serialization.Serializable

@Serializable
sealed interface GlanceWidgetState {
    @Serializable
    data object Initial : GlanceWidgetState

    @Serializable
    data object Loading : GlanceWidgetState

    @Serializable
    data object Success : GlanceWidgetState

    @Serializable
    data object Error : GlanceWidgetState
}