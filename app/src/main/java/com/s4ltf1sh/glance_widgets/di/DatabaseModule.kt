package com.s4ltf1sh.glance_widgets.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.s4ltf1sh.glance_widgets.db.AppDatabase
import com.s4ltf1sh.glance_widgets.db.DatabaseManager
import com.s4ltf1sh.glance_widgets.db.RoomDatabaseManager
import com.s4ltf1sh.glance_widgets.db.WidgetModelDao
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
annotation class AppCoroutineScope

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun providesAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "app.db")
            .addCallback(
                object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                    }
                },
            ).build()

    @Provides
    fun providesWidgetModelDao(database: AppDatabase): WidgetModelDao = database.widgetDao()

    @Provides
    @Singleton
    @AppCoroutineScope
    fun providesApplicationCoroutineScope(): CoroutineScope = CoroutineScope(
        Executors.newSingleThreadExecutor().asCoroutineDispatcher(),
    )
}

@Module
@InstallIn(SingletonComponent::class)
interface DatabaseBindingModule {

    @Binds
    fun bindDatabaseManager(manager: RoomDatabaseManager): DatabaseManager
}
