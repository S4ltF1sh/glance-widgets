package com.s4ltf1sh.glance_widgets.db.clock

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetSize
import kotlinx.coroutines.flow.Flow

@Dao
interface GlanceClockAnalogDao {
    @Query("SELECT * FROM clock_analog WHERE size = :size ORDER BY createdAt DESC")
    fun getClocksBySize(size: GlanceWidgetSize): Flow<List<GlanceClockAnalogEntity>>

    @Query("SELECT * FROM clock_analog WHERE id = :clockId")
    suspend fun getClockById(clockId: Long): GlanceClockAnalogEntity?

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertClock(
        clock: GlanceClockAnalogEntity
    )

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertClocks(
        clocks: List<GlanceClockAnalogEntity>
    )

    @Update(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun updateClock(
        clock: GlanceClockAnalogEntity
    )

    @Query("DELETE FROM clock_analog WHERE id = :clockId")
    suspend fun deleteClock(clockId: Long)
}