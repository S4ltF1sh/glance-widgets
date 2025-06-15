package com.s4ltf1sh.glance_widgets.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.s4ltf1sh.glance_widgets.db.photo.PhotoDao
import com.s4ltf1sh.glance_widgets.db.photo.PhotoEntity
import com.s4ltf1sh.glance_widgets.db.quote.QuoteDao
import com.s4ltf1sh.glance_widgets.db.quote.QuoteEntity

@Database(
    entities = [WidgetEntity::class, QuoteEntity::class, PhotoEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(WidgetConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun widgetDao(): WidgetDao
    abstract fun quoteDao(): QuoteDao
    abstract fun photoDao(): PhotoDao
}

fun RoomDatabase.wipeAndReinitializeData() = runInTransaction {
    clearAllTables()
}
