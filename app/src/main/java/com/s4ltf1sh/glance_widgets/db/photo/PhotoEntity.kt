package com.s4ltf1sh.glance_widgets.db.photo

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.s4ltf1sh.glance_widgets.model.WidgetSize

@Entity(tableName = "photos")
data class PhotoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val size: WidgetSize,
    val photoPaths: List<String>, // List of image paths
    val index: Int, // Index of the photo in the list
    val createdAt: Long = System.currentTimeMillis()
)

data class PhotoPaths(
    val photoPaths: List<String>
)