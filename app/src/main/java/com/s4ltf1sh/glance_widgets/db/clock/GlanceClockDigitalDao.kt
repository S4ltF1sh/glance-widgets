package com.s4ltf1sh.glance_widgets.db.clock

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetSize
import kotlinx.coroutines.flow.Flow

@Dao
interface GlanceClockDigitalDao {
    @Query("SELECT * FROM clock_digital WHERE size = :size ORDER BY createdAt DESC")
    fun getClocksBySize(size: GlanceWidgetSize): Flow<List<GlanceClockDigitalEntity>>

    @Query("SELECT * FROM clock_digital WHERE id = :clockId")
    suspend fun getClockById(clockId: Long): GlanceClockDigitalEntity?

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertClock(clock: GlanceClockDigitalEntity)

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertClocks(clocks: List<GlanceClockDigitalEntity>)

    @Update(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun updateClock(clock: GlanceClockDigitalEntity)

    @Query("DELETE FROM clock_digital WHERE id = :clockId")
    suspend fun deleteClock(clockId: Long)
}