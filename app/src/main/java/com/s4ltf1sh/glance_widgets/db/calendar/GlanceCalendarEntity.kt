package com.s4ltf1sh.glance_widgets.db.calendar

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetSize
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetType

@Entity(tableName = "calendars")
data class GlanceCalendarEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val size: GlanceWidgetSize,
    val type: GlanceWidgetType,
    val backgroundUrl: String, // URL or path to the background image
    val createdAt: Long = System.currentTimeMillis()
)