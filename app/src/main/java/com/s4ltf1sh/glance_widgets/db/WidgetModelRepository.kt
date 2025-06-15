package com.s4ltf1sh.glance_widgets.db

import android.content.Context
import com.s4ltf1sh.glance_widgets.db.photo.PhotoDao
import com.s4ltf1sh.glance_widgets.db.photo.PhotoEntity
import com.s4ltf1sh.glance_widgets.db.quote.QuoteDao
import com.s4ltf1sh.glance_widgets.db.quote.QuoteEntity
import com.s4ltf1sh.glance_widgets.di.AppCoroutineScope
import com.s4ltf1sh.glance_widgets.model.WidgetSize
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

    suspend fun getQuoteById(quoteId: Long): QuoteEntity? {
        return quoteDao.getQuoteById(quoteId)
    }

    suspend fun insertQuotes(quotes: List<QuoteEntity>) {
        quoteDao.insertQuotes(quotes)
    }

    fun quoteEntityToJson(quoteEntity: QuoteEntity): String {
        return quoteAdapter.toJson(quoteEntity)
    }

    fun quoteEntityFromJson(str: String): QuoteEntity? {
        return quoteAdapter.fromJson(str)
    }


    // Photo related methods
    suspend fun getPhotoById(photoId: Long): PhotoEntity? {
        return photoDao.getPhotoById(photoId)
    }

    suspend fun insertPhoto(photo: PhotoEntity) {
        photoDao.insertPhoto(photo)
    }

    suspend fun insertPhotos(photos: List<PhotoEntity>) {
        photoDao.insertPhotos(photos)
    }

    suspend fun updatePhoto(photo: PhotoEntity) {
        photoDao.updatePhoto(photo)
    }

    suspend fun deletePhoto(photoId: Long) {
        photoDao.deletePhoto(photoId)
    }
}