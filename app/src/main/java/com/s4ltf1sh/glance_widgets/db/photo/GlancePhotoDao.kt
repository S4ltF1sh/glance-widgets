package com.s4ltf1sh.glance_widgets.db.photo

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetSize
import kotlinx.coroutines.flow.Flow

@Dao
interface GlancePhotoDao {
    @Query("SELECT * FROM photos WHERE size = :size ORDER BY createdAt DESC")
    fun getPhotosBySize(size: GlanceWidgetSize): Flow<List<GlancePhotoEntity>>

    @Query("SELECT * FROM photos WHERE size = :size ORDER BY createdAt DESC")
    fun getPhotosBySizeOnce(size: GlanceWidgetSize): List<GlancePhotoEntity>

    @Query("SELECT * FROM photos WHERE id = :photoId")
    suspend fun getPhotoById(photoId: Long): GlancePhotoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: GlancePhotoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotos(photos: List<GlancePhotoEntity>)

    @Update
    suspend fun updatePhoto(photo: GlancePhotoEntity)

    @Query("DELETE FROM photos WHERE id = :photoId")
    suspend fun deletePhoto(photoId: Long)
}