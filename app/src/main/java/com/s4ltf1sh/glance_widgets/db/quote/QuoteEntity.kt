package com.s4ltf1sh.glance_widgets.db.quote

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.s4ltf1sh.glance_widgets.widget.model.WidgetSize

@Entity(tableName = "quotes")
data class QuoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val setId: String, // ID to group quotes by theme/set
    val setName: String, // Human-readable name for the set
    val size: WidgetSize,
    val imageUrl: String, // URL or path to the image
    val imageResourceName: String? = null, // If using local resources
    val isSelected: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)