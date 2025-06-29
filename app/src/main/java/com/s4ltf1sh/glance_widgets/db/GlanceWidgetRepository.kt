package com.s4ltf1sh.glance_widgets.db

import android.content.Context
import com.s4ltf1sh.glance_widgets.db.calendar.GlanceCalendarDao
import com.s4ltf1sh.glance_widgets.db.calendar.GlanceCalendarEntity
import com.s4ltf1sh.glance_widgets.db.clock.GlanceClockAnalogDao
import com.s4ltf1sh.glance_widgets.db.clock.GlanceClockAnalogEntity
import com.s4ltf1sh.glance_widgets.db.clock.GlanceClockDigitalDao
import com.s4ltf1sh.glance_widgets.db.clock.GlanceClockDigitalEntity
import com.s4ltf1sh.glance_widgets.db.photo.GlancePhotoDao
import com.s4ltf1sh.glance_widgets.db.quote.GlanceQuoteDao
import com.s4ltf1sh.glance_widgets.db.quote.GlanceQuoteEntity
import com.s4ltf1sh.glance_widgets.di.AppCoroutineScope
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetSize
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
class GlanceWidgetRepository @Inject internal constructor(
    private val glanceWidgetDao: GlanceWidgetDao,
    private val glanceQuoteDao: GlanceQuoteDao,
    private val glancePhotoDao: GlancePhotoDao,
    private val glanceClockDigitalDao: GlanceClockDigitalDao,
    private val glanceClockAnalogDao: GlanceClockAnalogDao,
    private val glanceCalendarDao: GlanceCalendarDao,
    private val moshi: Moshi,
    @AppCoroutineScope private val coroutineScope: CoroutineScope,
    @ApplicationContext private val appContext: Context,
) {
    private val quoteTypes = Types.newParameterizedType(GlanceQuoteEntity::class.java, String::class.java)
    private val quoteAdapter = moshi.adapter<GlanceQuoteEntity>(quoteTypes)

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface WidgetModelRepositoryEntryPoint {
        fun widgetModelRepository(): GlanceWidgetRepository
    }

    companion object {
        fun get(applicationContext: Context): GlanceWidgetRepository {
            var widgetModelRepositoryEntryPoint: WidgetModelRepositoryEntryPoint = EntryPoints.get(
                applicationContext,
                WidgetModelRepositoryEntryPoint::class.java,
            )
            return widgetModelRepositoryEntryPoint.widgetModelRepository()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getWidgetFlow(widgetId: Int) = glanceWidgetDao.getWidgetFlow(widgetId).distinctUntilChanged()

    suspend fun getWidget(widgetId: Int): GlanceWidgetEntity? = glanceWidgetDao.getWidget(widgetId)

    suspend fun getAllWidgets(): List<GlanceWidgetEntity> = glanceWidgetDao.getAllWidgets()

    suspend fun insertWidget(widget: GlanceWidgetEntity): Long = glanceWidgetDao.insertWidget(widget)

    suspend fun deleteWidgetById(widgetId: Int) = glanceWidgetDao.deleteWidgetById(widgetId)

    suspend fun updateWidget(widget: GlanceWidgetEntity): Int = withContext(Dispatchers.IO) {
        return@withContext glanceWidgetDao.updateWidget(widget)
    }

    // Quote related methods
    fun getQuotesBySize(size: GlanceWidgetSize): Flow<List<GlanceQuoteEntity>> {
        return glanceQuoteDao.getQuotesBySize(size)
    }

    suspend fun insertQuotes(quotes: List<GlanceQuoteEntity>) {
        glanceQuoteDao.insertQuotes(quotes)
    }

    // Clock digital related methods
    suspend fun insertClockDigital(clockDigital: GlanceClockDigitalEntity) {
        glanceClockDigitalDao.insertClock(clockDigital)
    }

    suspend fun insertClockDigitals(clockDigitals: List<GlanceClockDigitalEntity>) {
        glanceClockDigitalDao.insertClocks(clockDigitals)
    }

    fun getClockDigitalsBySize(size: GlanceWidgetSize): Flow<List<GlanceClockDigitalEntity>> {
        return glanceClockDigitalDao.getClocksBySize(size)
    }

    // Clock analog related methods
    suspend fun insertClockAnalog(clockAnalog: GlanceClockAnalogEntity) {
        glanceClockAnalogDao.insertClock(clockAnalog)
    }

    suspend fun insertClockAnalogs(clockAnalogs: List<GlanceClockAnalogEntity>) {
        glanceClockAnalogDao.insertClocks(clockAnalogs)
    }

    suspend fun getClockAnalogBySize(size: GlanceWidgetSize): Flow<List<GlanceClockAnalogEntity>> {
        return glanceClockAnalogDao.getClocksBySize(size)
    }

    // Calendar related methods
    fun getCalendarsBySize(size: GlanceWidgetSize): Flow<List<GlanceCalendarEntity>> {
        return glanceCalendarDao.getCalendarBySize(size)
    }

    suspend fun insertCalendar(calendar: GlanceCalendarEntity) {
        glanceCalendarDao.insertCalendar(calendar)
    }

    suspend fun insertCalendars(calendars: List<GlanceCalendarEntity>) {
        glanceCalendarDao.insertCalendars(calendars)
    }
}