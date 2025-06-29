package com.s4ltf1sh.glance_widgets.db.clock

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetSize
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetType

@Entity(tableName = "clock_analog")
data class GlanceClockAnalogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val size: GlanceWidgetSize,
    val type: GlanceWidgetType,
    val backgroundUrl: String,
    val createdAt: Long = System.currentTimeMillis()
)