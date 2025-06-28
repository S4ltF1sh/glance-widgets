package com.s4ltf1sh.glance_widgets.db.photo

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetSize

@Entity(tableName = "photos")
data class GlancePhotoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val size: GlanceWidgetSize,
    val photoPaths: List<String>, // List of image paths
    val index: Int, // Index of the photo in the list
    val createdAt: Long = System.currentTimeMillis()
)