package com.s4ltf1sh.glance_widgets.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.s4ltf1sh.glance_widgets.widget.model.WidgetSize
import com.s4ltf1sh.glance_widgets.widget.model.WidgetType

sealed interface WidgetState {
    object Empty : WidgetState
    object Loading : WidgetState
}

@Entity(tableName = "widgets")
data class WidgetEntity(
    @PrimaryKey val widgetId: Int,
    val type: WidgetType,
    val size: WidgetSize,
    val lastUpdated: Long = System.currentTimeMillis(),
    val data: String = "" // JSON data specific to widget type
): WidgetState