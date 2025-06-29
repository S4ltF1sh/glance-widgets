package com.s4ltf1sh.glance_widgets.db.calendar

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetSize
import kotlinx.coroutines.flow.Flow

@Dao
interface GlanceCalendarDao {
    @Query("SELECT * FROM calendars WHERE size = :size ORDER BY createdAt DESC")
    fun getCalendarBySize(size: GlanceWidgetSize): Flow<List<GlanceCalendarEntity>>

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertCalendar(calendar: GlanceCalendarEntity)

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertCalendars(calendars: List<GlanceCalendarEntity>)

    @Update(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun updateCalendar(calendar: GlanceCalendarEntity)

    @Delete
    suspend fun deleteCalendar(calendar: GlanceCalendarEntity)
}