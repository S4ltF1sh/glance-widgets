package com.s4ltf1sh.glance_widgets.db.clock

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.s4ltf1sh.glance_widgets.model.WidgetSize
import kotlinx.coroutines.flow.Flow

@Dao
interface ClockDigitalDao {
    @Query("SELECT * FROM clock_digital WHERE size = :size ORDER BY createdAt DESC")
    fun getClocksBySize(size: WidgetSize): Flow<List<ClockDigitalEntity>>

    @Query("SELECT * FROM clock_digital WHERE id = :clockId")
    suspend fun getClockById(clockId: Long): ClockDigitalEntity?

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertClock(clock: ClockDigitalEntity)

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertClocks(clocks: List<ClockDigitalEntity>)

    @Update(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun updateClock(clock: ClockDigitalEntity)

    @Query("DELETE FROM clock_digital WHERE id = :clockId")
    suspend fun deleteClock(clockId: Long)
}