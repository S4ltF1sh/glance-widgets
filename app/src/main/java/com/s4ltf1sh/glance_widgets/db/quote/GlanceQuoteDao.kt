package com.s4ltf1sh.glance_widgets.db.quote

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetSize
import kotlinx.coroutines.flow.Flow

@Dao
interface GlanceQuoteDao {
    @Query("SELECT * FROM quotes WHERE size = :size ORDER BY createdAt DESC")
    fun getQuotesBySize(size: GlanceWidgetSize): Flow<List<GlanceQuoteEntity>>

    @Query("SELECT * FROM quotes WHERE id = :quoteId")
    suspend fun getQuoteById(quoteId: Long): GlanceQuoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuote(quote: GlanceQuoteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuotes(quotes: List<GlanceQuoteEntity>)

    @Update
    suspend fun updateQuote(quote: GlanceQuoteEntity)

    @Query("DELETE FROM quotes WHERE id = :quoteId")
    suspend fun deleteQuote(quoteId: Long)
}