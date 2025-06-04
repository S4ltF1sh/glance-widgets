package com.s4ltf1sh.glance_widgets.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [WidgetEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(WidgetConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun widgetDao(): WidgetModelDao
}

fun RoomDatabase.wipeAndReinitializeData() = runInTransaction {
    clearAllTables()
}
