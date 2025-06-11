package com.s4ltf1sh.glance_widgets.db.quote

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.s4ltf1sh.glance_widgets.widget.model.WidgetSize
import com.s4ltf1sh.glance_widgets.widget.model.quotes.QuoteSetInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface QuoteDao {
    @Query("SELECT * FROM quotes WHERE size = :size ORDER BY setName, createdAt DESC")
    fun getQuotesBySize(size: WidgetSize): Flow<List<QuoteEntity>>

    @Query("SELECT * FROM quotes WHERE size = :size ORDER BY setName, createdAt DESC")
    fun getQuotesBySizeOnce(size: WidgetSize): List<QuoteEntity>

    @Query("SELECT * FROM quotes WHERE id = :quoteId")
    suspend fun getQuoteById(quoteId: Long): QuoteEntity?

    @Query("SELECT * FROM quotes WHERE setId = :setId")
    suspend fun getQuotesBySetId(setId: String): List<QuoteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuote(quote: QuoteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuotes(quotes: List<QuoteEntity>)

    @Update
    suspend fun updateQuote(quote: QuoteEntity)

    @Query("DELETE FROM quotes WHERE id = :quoteId")
    suspend fun deleteQuote(quoteId: Long)

    @Query("SELECT DISTINCT setId, setName FROM quotes ORDER BY setName")
    suspend fun getAllSets(): List<QuoteSetInfo>
}