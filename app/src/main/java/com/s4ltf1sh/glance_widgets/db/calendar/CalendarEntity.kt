package com.s4ltf1sh.glance_widgets.db.calendar

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.s4ltf1sh.glance_widgets.model.WidgetSize
import com.s4ltf1sh.glance_widgets.model.WidgetType

@Entity(tableName = "calendars")
data class CalendarEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val size: WidgetSize,
    val type: WidgetType,
    val backgroundUrl: String, // URL or path to the background image
    val createdAt: Long = System.currentTimeMillis()
)