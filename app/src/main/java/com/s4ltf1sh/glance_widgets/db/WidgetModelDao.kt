package com.s4ltf1sh.glance_widgets.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.s4ltf1sh.glance_widgets.widget.model.WidgetType

@Dao
interface WidgetModelDao {
    @Query("SELECT * FROM widgets WHERE widgetId = :widgetId")
    suspend fun getWidget(widgetId: Int): WidgetEntity?

    @Query("SELECT * FROM widgets")
    suspend fun getAllWidgets(): List<WidgetEntity>

    @Query("SELECT * FROM widgets WHERE type = :type")
    suspend fun getWidgetsByType(type: WidgetType): List<WidgetEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWidget(widget: WidgetEntity)

    @Delete
    suspend fun deleteWidget(widget: WidgetEntity)

    @Query("DELETE FROM widgets WHERE widgetId = :widgetId")
    suspend fun deleteWidgetById(widgetId: Int)
}