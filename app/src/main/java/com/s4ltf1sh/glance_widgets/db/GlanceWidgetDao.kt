package com.s4ltf1sh.glance_widgets.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetType
import kotlinx.coroutines.flow.Flow

@Dao
interface GlanceWidgetDao {
    @Query("SELECT * FROM glance_widgets WHERE widgetId = :widgetId")
    fun getWidgetFlow(widgetId: Int): Flow<GlanceWidgetEntity?>

    @Query("SELECT * FROM glance_widgets WHERE widgetId = :widgetId")
    suspend fun getWidget(widgetId: Int): GlanceWidgetEntity?

    @Query("SELECT * FROM glance_widgets")
    suspend fun getAllWidgets(): List<GlanceWidgetEntity>

    @Query("SELECT * FROM glance_widgets WHERE type = :type")
    suspend fun getWidgetsByType(type: GlanceWidgetType): List<GlanceWidgetEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWidget(widget: GlanceWidgetEntity): Long

    @Delete
    suspend fun deleteWidget(widget: GlanceWidgetEntity)

    @Query("DELETE FROM glance_widgets WHERE widgetId = :widgetId")
    suspend fun deleteWidgetById(widgetId: Int)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateWidget(widget: GlanceWidgetEntity): Int
}