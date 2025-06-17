package com.s4ltf1sh.glance_widgets.db.clock

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.s4ltf1sh.glance_widgets.model.WidgetSize
import com.s4ltf1sh.glance_widgets.model.WidgetType

@Entity(tableName = "clock_digital")
data class ClockDigitalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val size: WidgetSize,
    val type: WidgetType,
    val backgroundUrl: String,
    val createdAt: Long = System.currentTimeMillis()
)