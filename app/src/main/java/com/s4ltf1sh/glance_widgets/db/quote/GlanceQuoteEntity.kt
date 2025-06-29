package com.s4ltf1sh.glance_widgets.db.quote

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetSize

@Entity(tableName = "quotes")
data class GlanceQuoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val size: GlanceWidgetSize,
    val imageUrl: String, // URL or path to the image
    val createdAt: Long = System.currentTimeMillis()
)