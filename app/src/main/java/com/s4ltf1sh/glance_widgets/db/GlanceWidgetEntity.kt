package com.s4ltf1sh.glance_widgets.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.s4ltf1sh.glance_widgets.model.GlanceWidget
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetSize
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetType

@Entity(tableName = "glance_widgets")
data class GlanceWidgetEntity(
    @PrimaryKey val widgetId: Int,
    val type: GlanceWidgetType,
    val size: GlanceWidgetSize,
    val lastUpdated: Long = System.currentTimeMillis(),
    val data: String = "" // JSON data specific to widget type
) {
    fun toWidget(): GlanceWidget {
        return GlanceWidget(
            widgetId = widgetId,
            type = type,
            size = size,
            lastUpdated = lastUpdated,
            data = data
        )
    }

    companion object {
        val EMPTY = GlanceWidgetEntity(
            widgetId = -1,
            type = GlanceWidgetType.None,
            size = GlanceWidgetSize.SMALL
        )
    }
}