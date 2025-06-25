package com.s4ltf1sh.glance_widgets.db.calendar

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.s4ltf1sh.glance_widgets.model.WidgetSize
import kotlinx.coroutines.flow.Flow

@Dao
interface CalendarDao {
    @Query("SELECT * FROM calendars WHERE size = :size ORDER BY createdAt DESC")
    fun getCalendarBySize(size: WidgetSize): Flow<List<CalendarEntity>>

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertCalendar(calendar: CalendarEntity)

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertCalendars(calendars: List<CalendarEntity>)

    @Update(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun updateCalendar(calendar: CalendarEntity)

    @Delete
    suspend fun deleteCalendar(calendar: CalendarEntity)
}