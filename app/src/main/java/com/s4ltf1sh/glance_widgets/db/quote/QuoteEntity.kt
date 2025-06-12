package com.s4ltf1sh.glance_widgets.db.quote

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.s4ltf1sh.glance_widgets.model.WidgetSize

@Entity(tableName = "quotes")
data class QuoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val size: WidgetSize,
    val imageUrl: String, // URL or path to the image
    val createdAt: Long = System.currentTimeMillis()
)