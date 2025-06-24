package com.s4ltf1sh.glance_widgets.db.calendar

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.s4ltf1sh.glance_widgets.model.WidgetSize
import com.s4ltf1sh.glance_widgets.model.WidgetType

@Entity(tableName = "calendar_backgrounds")
data class CalendarEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val size: WidgetSize,
    val type: WidgetType.Calendar,
    val backgroundUrl: String, // URL or path to the background image
    val name: String = "", // Optional name for the background
    val createdAt: Long = System.currentTimeMillis()
)