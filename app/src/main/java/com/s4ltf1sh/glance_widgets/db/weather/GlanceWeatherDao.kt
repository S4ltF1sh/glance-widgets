package com.s4ltf1sh.glance_widgets.db.weather

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetSize
import kotlinx.coroutines.flow.Flow

@Dao
interface GlanceWeatherDao {
    @Query("SELECT * FROM weather WHERE size = :size ORDER BY createdAt DESC")
    fun getWeatherBySize(size: GlanceWidgetSize): Flow<List<GlanceWeatherEntity>>

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: GlanceWeatherEntity)

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertWeathers(weathers: List<GlanceWeatherEntity>)

    @Update(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun updateWeather(weather: GlanceWeatherEntity)

    @Delete
    suspend fun deleteWeather(weather: GlanceWeatherEntity)
}