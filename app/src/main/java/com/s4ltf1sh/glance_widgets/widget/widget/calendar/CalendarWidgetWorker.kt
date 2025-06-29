package com.s4ltf1sh.glance_widgets.widget.widget.calendar

import android.content.Context
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.s4ltf1sh.glance_widgets.db.GlanceWidgetEntity
import com.s4ltf1sh.glance_widgets.db.GlanceWidgetRepository
import com.s4ltf1sh.glance_widgets.db.calendar.WidgetCalendarData
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetSize
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetType
import com.s4ltf1sh.glance_widgets.utils.downloadImageWithCoil
import com.s4ltf1sh.glance_widgets.utils.updateWidgetUI
import com.s4ltf1sh.glance_widgets.widget.core.BaseAppWidget
import com.s4ltf1sh.glance_widgets.widget.core.setWidgetError
import com.s4ltf1sh.glance_widgets.widget.core.setWidgetSuccess
import com.squareup.moshi.Moshi
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay

private enum class WorkType() {
    INIT,
    UPDATE_TIME
}

@HiltWorker
class CalendarWidgetWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    val moshi: Moshi
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val WIDGET_ID = "widget_id"
        private const val WIDGET_TYPE = "widget_type"
        private const val WIDGET_SIZE = "widget_size"
        private const val LASTED_UPDATE = "lasted_update"
        private const val WORK_TYPE = "work_type"
        private const val BACKGROUND_IMAGE_URL = "background_image_url"

        private const val TAG = "CalendarWidgetWorker"

        // Retry configuration
        private const val MAX_RETRY_COUNT = 3
        private const val RETRY_DELAY_MS = 1000L

        fun enqueue(
            context: Context,
            widgetId: Int,
            type: GlanceWidgetType,
            glanceWidgetSize: GlanceWidgetSize,
            backgroundImageUrl: String? = null
        ) {
            Log.d(TAG, "Enqueuing calendar work for widget ID: $widgetId")

            val workManager = WorkManager.getInstance(context)
            val widgetWorkerName = BaseAppWidget.getWidgetWorkerName(widgetId)

            val request = OneTimeWorkRequestBuilder<CalendarWidgetWorker>().apply {
                addTag(widgetWorkerName)
                setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                setInputData(
                    Data.Builder()
                        .putInt(WIDGET_ID, widgetId)
                        .putString(WORK_TYPE, WorkType.INIT.name)
                        .putString(WIDGET_TYPE, type.typeId)
                        .putString(WIDGET_SIZE, glanceWidgetSize.name)
                        .putString(BACKGROUND_IMAGE_URL, backgroundImageUrl ?: "")
                        .build()
                )
            }.build()

            workManager.enqueueUniqueWork(
                uniqueWorkName = widgetWorkerName,
                existingWorkPolicy = ExistingWorkPolicy.REPLACE,
                request = request
            )
        }

        fun updateTime(
            context: Context,
            widgetId: Int,
            lastedUpdate: Long
        ) {
            Log.d(TAG, "Updating time for widget ID: $widgetId, lastedUpdate: $lastedUpdate")

            val workManager = WorkManager.getInstance(context)
            val widgetWorkerName = BaseAppWidget.getWidgetWorkerName(widgetId)

            workManager.cancelUniqueWork(widgetWorkerName)

            val request = OneTimeWorkRequestBuilder<CalendarWidgetWorker>().apply {
                addTag(widgetWorkerName)
                setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                setInputData(
                    Data.Builder()
                        .putInt(WIDGET_ID, widgetId)
                        .putString(WORK_TYPE, WorkType.UPDATE_TIME.name)
                        .putLong(LASTED_UPDATE, lastedUpdate)
                        .build()
                )
            }.build()

            workManager.enqueueUniqueWork(
                uniqueWorkName = widgetWorkerName,
                existingWorkPolicy = ExistingWorkPolicy.REPLACE,
                request = request
            )
        }
    }

    override suspend fun doWork(): Result {
        val widgetId = inputData.getInt(WIDGET_ID, -1)

        if (widgetId == -1) {
            Log.e(TAG, "Invalid widget ID")
            return Result.failure()
        }

        val glanceId = GlanceAppWidgetManager(context).getGlanceIdBy(widgetId)

        val workType = inputData.getString(WORK_TYPE)?.let { WorkType.valueOf(it) }
            ?: run {
                Log.e(TAG, "Invalid or missing work type for widget ID: $widgetId")
                return Result.failure()
            }

        return when (workType) {
            WorkType.INIT -> {
                setupNewCalendar(widgetId, glanceId)
            }

            WorkType.UPDATE_TIME -> {
                updateWidgetDataTime(widgetId)
            }
        }


    }

    private suspend fun setupNewCalendar(
        widgetId: Int,
        glanceId: GlanceId
    ): Result {
        try {
            val widgetType =
                inputData.getString(WIDGET_TYPE)?.let { GlanceWidgetType.fromTypeId(it) } ?: run {
                    Log.e(TAG, "Invalid or missing widget type for widget ID: $widgetId")
                    return Result.failure()
                }

            val widgetSize = inputData.getString(WIDGET_SIZE)?.let { GlanceWidgetSize.valueOf(it) }
                ?: run {
                    Log.w(TAG, "Widget size not provided, defaulting to MEDIUM")
                    return Result.failure()
                }

            val backgroundImageUrl =
                inputData.getString(BACKGROUND_IMAGE_URL)?.takeIf { it.isNotEmpty() }

            // Download background image if provided
            val backgroundImagePath = backgroundImageUrl?.let { url ->
                downloadImageWithRetry(url)
            }

            // Create calendar data
            val calendarData = WidgetCalendarData(backgroundPath = backgroundImagePath)

            // Update widget data
            val updated = updateWidgetData(widgetId, widgetType, calendarData)
            if (updated == null) {
                Log.e(TAG, "Failed to update widget data for widget: $widgetId")
                context.setWidgetError(
                    glanceId = glanceId,
                    glanceWidgetSize = widgetSize,
                    message = "Failed to update widget data",
                    throwable = NullPointerException("Widget with ID $widgetId does not exist")
                )
                return Result.failure()
            }

            // Update widget UI
            val updateUISuccess = context.updateWidgetUI(widgetId, widgetSize)

            if (!updateUISuccess) {
                Log.e(TAG, "Failed to update widget UI for widget: $widgetId")
                context.setWidgetError(
                    glanceId = glanceId,
                    glanceWidgetSize = widgetSize,
                    message = "Failed to update widget UI",
                    throwable = Exception("Update UI failed for widget ID $widgetId")
                )
                return Result.failure()
            } else {
                Log.d(TAG, "Calendar widget $widgetId updated successfully")
                context.setWidgetSuccess(
                    glanceId = glanceId,
                    glanceWidgetSize = widgetSize,
                    widget = updated
                )
                return Result.success()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error in calendar setup", e)

            // Retry if under limit
            if (runAttemptCount < MAX_RETRY_COUNT) {
                Log.d(TAG, "Retrying... Attempt ${runAttemptCount + 1}/$MAX_RETRY_COUNT")
                return Result.retry()
            }

            return Result.failure()
        }
    }

    private suspend fun downloadImageWithRetry(url: String): String? {
        var lastError: Exception? = null

        repeat(MAX_RETRY_COUNT) { attempt ->
            try {
                val imagePath = context.downloadImageWithCoil(
                    url = url,
                    force = true
                )

                if (imagePath.isNotEmpty()) {
                    return imagePath
                }
            } catch (e: Exception) {
                lastError = e
                Log.w(TAG, "Download attempt ${attempt + 1} failed: ${e.message}")
            }

            // Wait before retry (except for last attempt)
            if (attempt < MAX_RETRY_COUNT - 1) {
                delay(RETRY_DELAY_MS * (attempt + 1)) // Exponential backoff
            }
        }

        Log.e(TAG, "All download attempts failed", lastError)
        return null
    }

    private suspend fun updateWidgetData(
        widgetId: Int,
        widgetType: GlanceWidgetType,
        calendarData: WidgetCalendarData
    ): GlanceWidgetEntity? {
        return try {
            val repo = GlanceWidgetRepository.get(context)
            val widget = repo.getWidget(widgetId)

            val updatedWidget = widget?.copy(
                type = widgetType,
                data = moshi
                    .adapter(WidgetCalendarData::class.java)
                    .toJson(calendarData),
                lastUpdated = System.currentTimeMillis()
            )

            if (updatedWidget == null) {
                Log.e(TAG, "Widget not found for ID: $widgetId")
                null
            } else {
                Log.d(TAG, "Updating calendar widget data for ID: $widgetId")
                repo.insertWidget(updatedWidget)
                updatedWidget
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating calendar widget data", e)
            null
        }
    }

    private suspend fun updateWidgetDataTime(widgetId: Int): Result {
        try {
            val lastedUpdate = inputData.getLong(LASTED_UPDATE, -1L)

            if (lastedUpdate == -1L) {
                Log.e(TAG, "Invalid or missing lasted update time for widget ID: $widgetId")
                return Result.failure()
            }

            val repo = GlanceWidgetRepository.get(context)
            val widget = repo.getWidget(widgetId)

            if(widget == null) {
                Log.e(TAG, "Widget not found for ID: $widgetId")
                return Result.failure()
            }

            val widgetSize = widget.size

            val calendarData = widget.data.let {
                moshi.adapter(WidgetCalendarData::class.java).fromJson(it)
            } ?: WidgetCalendarData.Init()

            val newData = calendarData.copy(
                lastedUpdate = lastedUpdate
            )

            val updatedWidget = widget.copy(
                data = moshi
                    .adapter(WidgetCalendarData::class.java)
                    .toJson(newData),
                lastUpdated = System.currentTimeMillis()
            )

            Log.d(TAG, "Updating calendar widget data for ID: $widgetId")
            val insertedSuccess = repo.updateWidget(updatedWidget)

            if (insertedSuccess == -1) {
                Log.e(TAG, "Failed to update widget data for ID: $widgetId")
                return Result.failure()
            }

            Log.d(TAG, "Calendar widget data updated successfully for ID: $widgetId, lastedUpdate: $lastedUpdate")

            context.updateWidgetUI(widgetId, widgetSize)
            return Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error updating calendar widget data", e)
            return Result.failure()
        }
    }
}