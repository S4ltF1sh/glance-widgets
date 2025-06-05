package com.s4ltf1sh.glance_widgets.db

import android.content.Context
import com.s4ltf1sh.glance_widgets.di.AppCoroutineScope
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WidgetModelRepository @Inject internal constructor(
    private val widgetModelDao: WidgetModelDao,
    @AppCoroutineScope private val coroutineScope: CoroutineScope,
    @ApplicationContext private val appContext: Context,
) {
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
    fun getWidgetFlow(widgetId: Int) = widgetModelDao.getWidgetFlow(widgetId).distinctUntilChanged()

    suspend fun getWidget(widgetId: Int): WidgetEntity? = widgetModelDao.getWidget(widgetId)

    suspend fun insertWidget(widget: WidgetEntity) = widgetModelDao.insertWidget(widget)

    suspend fun deleteWidget(widget: WidgetEntity) = widgetModelDao.deleteWidget(widget)

    suspend fun deleteWidgetById(widgetId: Int) = widgetModelDao.deleteWidgetById(widgetId)
}