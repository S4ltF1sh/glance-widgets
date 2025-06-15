package com.s4ltf1sh.glance_widgets.db.photo

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.s4ltf1sh.glance_widgets.model.WidgetSize
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {
    @Query("SELECT * FROM photos WHERE size = :size ORDER BY createdAt DESC")
    fun getPhotosBySize(size: WidgetSize): Flow<List<PhotoEntity>>

    @Query("SELECT * FROM photos WHERE size = :size ORDER BY createdAt DESC")
    fun getPhotosBySizeOnce(size: WidgetSize): List<PhotoEntity>

    @Query("SELECT * FROM photos WHERE id = :photoId")
    suspend fun getPhotoById(photoId: Long): PhotoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: PhotoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotos(photos: List<PhotoEntity>)

    @Update
    suspend fun updatePhoto(photo: PhotoEntity)

    @Query("DELETE FROM photos WHERE id = :photoId")
    suspend fun deletePhoto(photoId: Long)
}