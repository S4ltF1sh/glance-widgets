package com.s4ltf1sh.glance_widgets.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.s4ltf1sh.glance_widgets.db.calendar.GlanceCalendarDao
import com.s4ltf1sh.glance_widgets.db.calendar.GlanceCalendarEntity
import com.s4ltf1sh.glance_widgets.db.clock.GlanceClockAnalogDao
import com.s4ltf1sh.glance_widgets.db.clock.GlanceClockAnalogEntity
import com.s4ltf1sh.glance_widgets.db.clock.GlanceClockDigitalDao
import com.s4ltf1sh.glance_widgets.db.clock.GlanceClockDigitalEntity
import com.s4ltf1sh.glance_widgets.db.photo.GlancePhotoDao
import com.s4ltf1sh.glance_widgets.db.photo.GlancePhotoEntity
import com.s4ltf1sh.glance_widgets.db.quote.GlanceQuoteDao
import com.s4ltf1sh.glance_widgets.db.quote.GlanceQuoteEntity

@Database(
    entities = [
        GlanceWidgetEntity::class,
        GlanceQuoteEntity::class,
        GlancePhotoEntity::class,
        GlanceClockDigitalEntity::class,
        GlanceClockAnalogEntity::class,
        GlanceCalendarEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(WidgetConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun glanceWidgetDao(): GlanceWidgetDao
    abstract fun glanceQuoteDao(): GlanceQuoteDao
    abstract fun glancePhotoDao(): GlancePhotoDao
    abstract fun glanceClockDigitalDao(): GlanceClockDigitalDao
    abstract fun glanceClockAnalogDao(): GlanceClockAnalogDao
    abstract fun glanceCalendarDao(): GlanceCalendarDao
}

fun RoomDatabase.wipeAndReinitializeData() = runInTransaction {
    clearAllTables()
}
