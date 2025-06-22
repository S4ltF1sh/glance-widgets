package com.s4ltf1sh.glance_widgets.db.clock

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.s4ltf1sh.glance_widgets.model.WidgetSize
import kotlinx.coroutines.flow.Flow

@Dao
interface ClockAnalogDao {
    @Query("SELECT * FROM clock_analog WHERE size = :size ORDER BY createdAt DESC")
    fun getClocksBySize(size: WidgetSize): Flow<List<ClockAnalogEntity>>

    @Query("SELECT * FROM clock_analog WHERE id = :clockId")
    suspend fun getClockById(clockId: Long): ClockAnalogEntity?

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertClock(
        clock: ClockAnalogEntity
    )

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertClocks(
        clocks: List<ClockAnalogEntity>
    )

    @Update(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun updateClock(
        clock: ClockAnalogEntity
    )

    @Query("DELETE FROM clock_analog WHERE id = :clockId")
    suspend fun deleteClock(clockId: Long)
}