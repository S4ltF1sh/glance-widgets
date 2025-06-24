package com.s4ltf1sh.glance_widgets.db

import android.content.Context
import com.s4ltf1sh.glance_widgets.db.clock.ClockAnalogDao
import com.s4ltf1sh.glance_widgets.db.clock.ClockAnalogEntity
import com.s4ltf1sh.glance_widgets.db.clock.ClockDigitalDao
import com.s4ltf1sh.glance_widgets.db.clock.ClockDigitalEntity
import com.s4ltf1sh.glance_widgets.db.photo.PhotoDao
import com.s4ltf1sh.glance_widgets.db.quote.QuoteDao
import com.s4ltf1sh.glance_widgets.db.quote.QuoteEntity
import com.s4ltf1sh.glance_widgets.di.AppCoroutineScope
import com.s4ltf1sh.glance_widgets.model.WidgetSize
import com.s4ltf1sh.glance_widgets.model.WidgetType
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WidgetModelRepository @Inject internal constructor(
    private val widgetDao: WidgetDao,
    private val quoteDao: QuoteDao,
    private val photoDao: PhotoDao,
    private val clockDigitalDao: ClockDigitalDao,
    private val clockAnalogDao: ClockAnalogDao,
    private val moshi: Moshi,
    @AppCoroutineScope private val coroutineScope: CoroutineScope,
    @ApplicationContext private val appContext: Context,
) {
    private val quoteTypes = Types.newParameterizedType(QuoteEntity::class.java, String::class.java)
    private val quoteAdapter = moshi.adapter<QuoteEntity>(quoteTypes)

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface WidgetModelRepositoryEntryPoint {
        fun widgetModelRepository(): WidgetModelRepository
    }

    companion object {
        fun get(applicationContext: Context): WidgetModelRepository {
            var widgetModelRepositoryEntryPoint: WidgetModelRepositoryEntryPoint = EntryPoints.get(
                applicationContext,
                WidgetModelRepositoryEntryPoint::class.java,
            )
            return widgetModelRepositoryEntryPoint.widgetModelRepository()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getWidgetFlow(widgetId: Int) = widgetDao.getWidgetFlow(widgetId).distinctUntilChanged()

    suspend fun getWidget(widgetId: Int): WidgetEntity? = widgetDao.getWidget(widgetId)

    suspend fun getAllWidgets(): List<WidgetEntity> = widgetDao.getAllWidgets()

    suspend fun getWidgetsByType(type: WidgetType): List<WidgetEntity> =
        widgetDao.getWidgetsByType(type)

    suspend fun insertWidget(widget: WidgetEntity): Long = widgetDao.insertWidget(widget)

    suspend fun deleteWidget(widget: WidgetEntity) = widgetDao.deleteWidget(widget)

    suspend fun deleteWidgetById(widgetId: Int) = widgetDao.deleteWidgetById(widgetId)

    suspend fun updateWidget(widget: WidgetEntity): Int = withContext(Dispatchers.IO) {
        return@withContext widgetDao.updateWidget(widget)
    }

    // Quote related methods
    fun getQuotesBySize(size: WidgetSize): Flow<List<QuoteEntity>> {
        return quoteDao.getQuotesBySize(size)
    }

    suspend fun insertQuotes(quotes: List<QuoteEntity>) {
        quoteDao.insertQuotes(quotes)
    }

    // Clock digital related methods
    suspend fun insertClockDigital(clockDigital: ClockDigitalEntity) {
        clockDigitalDao.insertClock(clockDigital)
    }

    suspend fun insertClockDigitals(clockDigitals: List<ClockDigitalEntity>) {
        clockDigitalDao.insertClocks(clockDigitals)
    }

    fun getClockDigitalsBySize(size: WidgetSize): Flow<List<ClockDigitalEntity>> {
        return clockDigitalDao.getClocksBySize(size)
    }

    // Clock analog related methods
    suspend fun insertClockAnalog(clockAnalog: ClockAnalogEntity) {
        clockAnalogDao.insertClock(clockAnalog)
    }

    suspend fun insertClockAnalogs(clockAnalogs: List<ClockAnalogEntity>) {
        clockAnalogDao.insertClocks(clockAnalogs)
    }

    suspend fun getClockAnalogBySize(size: WidgetSize): Flow<List<ClockAnalogEntity>> {
        return clockAnalogDao.getClocksBySize(size)
    }
}