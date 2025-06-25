package com.s4ltf1sh.glance_widgets.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.s4ltf1sh.glance_widgets.db.calendar.CalendarDao
import com.s4ltf1sh.glance_widgets.db.calendar.CalendarEntity
import com.s4ltf1sh.glance_widgets.db.clock.ClockAnalogDao
import com.s4ltf1sh.glance_widgets.db.clock.ClockAnalogEntity
import com.s4ltf1sh.glance_widgets.db.clock.ClockDigitalDao
import com.s4ltf1sh.glance_widgets.db.clock.ClockDigitalEntity
import com.s4ltf1sh.glance_widgets.db.photo.PhotoDao
import com.s4ltf1sh.glance_widgets.db.photo.PhotoEntity
import com.s4ltf1sh.glance_widgets.db.quote.QuoteDao
import com.s4ltf1sh.glance_widgets.db.quote.QuoteEntity

@Database(
    entities = [
        WidgetEntity::class,
        QuoteEntity::class,
        PhotoEntity::class,
        ClockDigitalEntity::class,
        ClockAnalogEntity::class,
        CalendarEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(WidgetConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun widgetDao(): WidgetDao
    abstract fun quoteDao(): QuoteDao
    abstract fun photoDao(): PhotoDao
    abstract fun clockDigitalDao(): ClockDigitalDao
    abstract fun clockAnalogDao(): ClockAnalogDao
    abstract fun calendarDao(): CalendarDao
}

fun RoomDatabase.wipeAndReinitializeData() = runInTransaction {
    clearAllTables()
}
