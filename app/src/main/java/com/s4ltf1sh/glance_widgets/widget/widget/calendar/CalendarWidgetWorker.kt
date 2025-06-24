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
import com.s4ltf1sh.glance_widgets.db.WidgetEntity
import com.s4ltf1sh.glance_widgets.db.WidgetModelRepository
import com.s4ltf1sh.glance_widgets.db.calendar.WidgetCalendarData
import com.s4ltf1sh.glance_widgets.model.WidgetSize
import com.s4ltf1sh.glance_widgets.model.WidgetType
import com.s4ltf1sh.glance_widgets.utils.CalendarUtils
import com.s4ltf1sh.glance_widgets.utils.downloadImageWithCoil
import com.s4ltf1sh.glance_widgets.utils.updateWidgetUI
import com.s4ltf1sh.glance_widgets.widget.core.BaseAppWidget
import com.s4ltf1sh.glance_widgets.widget.core.setWidgetError
import com.s4ltf1sh.glance_widgets.widget.core.setWidgetSuccess
import com.squareup.moshi.Moshi
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import java.util.Calendar

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
        private const val YEAR = "year"
        private const val MONTH = "month"
        private const val SELECTED_DAY = "selected_day"
        private const val BACKGROUND_IMAGE_URL = "background_image_url"

        private const val TAG = "CalendarWidgetWorker"

        // Retry configuration
        private const val MAX_RETRY_COUNT = 3
        private const val RETRY_DELAY_MS = 1000L

        fun enqueue(
            context: Context,
            widgetId: Int,
            type: WidgetType,
            widgetSize: WidgetSize,
            year: Int? = null,
            month: Int? = null,
            selectedDay: Int? = null,
            backgroundImageUrl: String? = null
        ) {
            Log.d(TAG, "Enqueuing calendar work for widget ID: $widgetId")

            val workManager = WorkManager.getInstance(context)
            val widgetWorkerName = BaseAppWidget.getWidgetWorkerName(widgetId)

            val currentDate = CalendarUtils.getCurrentDateInfo()
            val actualYear = year ?: currentDate.first
            val actualMonth = month ?: currentDate.second

            val request = OneTimeWorkRequestBuilder<CalendarWidgetWorker>().apply {
                addTag(widgetWorkerName)
                setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                setInputData(
                    Data.Builder()
                        .putInt(WIDGET_ID, widgetId)
                        .putString(WIDGET_TYPE, type.typeId)
                        .putString(WIDGET_SIZE, widgetSize.name)
                        .putInt(YEAR, actualYear)
                        .putInt(MONTH, actualMonth)
                        .putInt(SELECTED_DAY, selectedDay ?: -1)
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

        fun enqueueUpdate(
            context: Context,
            widgetId: Int
        ) {
            Log.d(TAG, "Enqueuing calendar update for widget ID: $widgetId")

            val workManager = WorkManager.getInstance(context)
            val widgetWorkerName = BaseAppWidget.getWidgetWorkerName(widgetId)

            val currentDate = CalendarUtils.getCurrentDateInfo()

            val request = OneTimeWorkRequestBuilder<CalendarWidgetWorker>().apply {
                addTag(widgetWorkerName)
                setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                setInputData(
                    Data.Builder()
                        .putInt(WIDGET_ID, widgetId)
                        .putString(WIDGET_TYPE, "UPDATE_ONLY")
                        .putInt(YEAR, currentDate.first)
                        .putInt(MONTH, currentDate.second)
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
        val widgetType = inputData.getString(WIDGET_TYPE)

        return if (widgetType == "UPDATE_ONLY") {
            updateExistingCalendar(widgetId, glanceId)
        } else {
            setupNewCalendar(widgetId, glanceId)
        }
    }

    private suspend fun setupNewCalendar(widgetId: Int, glanceId: GlanceId): Result {
        try {
            val widgetSize = try {
                inputData.getString(WIDGET_SIZE)?.let { WidgetSize.valueOf(it) }
                    ?: WidgetSize.MEDIUM
            } catch (e: Exception) {
                Log.e(TAG, "Invalid widget size", e)
                WidgetSize.MEDIUM
            }

            val year = inputData.getInt(YEAR, Calendar.getInstance().get(Calendar.YEAR))
            val month = inputData.getInt(MONTH, Calendar.getInstance().get(Calendar.MONTH) + 1)
            val selectedDay = inputData.getInt(SELECTED_DAY, -1).takeIf { it != -1 }
            val backgroundImageUrl = inputData.getString(BACKGROUND_IMAGE_URL)?.takeIf { it.isNotEmpty() }

            // Download background image if provided
            val backgroundImagePath = backgroundImageUrl?.let { url ->
                downloadImageWithRetry(url)
            }

            // Create calendar data
            val currentDate = CalendarUtils.getCurrentDateInfo()
            val calendarData = WidgetCalendarData(
                year = year,
                month = month,
                selectedDay = selectedDay,
                todayDay = if (year == currentDate.first && month == currentDate.second) {
                    currentDate.third
                } else null,
                backgroundPath = backgroundImagePath
            )

            // Update widget data
            val updated = updateWidgetData(widgetId, calendarData)
            if (updated == null) {
                Log.e(TAG, "Failed to update widget data for widget: $widgetId")
                context.setWidgetError(
                    glanceId = glanceId,
                    widgetSize = widgetSize,
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
                    widgetSize = widgetSize,
                    message = "Failed to update widget UI",
                    throwable = Exception("Update UI failed for widget ID $widgetId")
                )
                return Result.failure()
            } else {
                Log.d(TAG, "Calendar widget $widgetId updated successfully")
                context.setWidgetSuccess(
                    glanceId = glanceId,
                    widgetSize = widgetSize,
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

    private suspend fun updateExistingCalendar(widgetId: Int, glanceId: GlanceId): Result {
        try {
            val repo = WidgetModelRepository.get(context)
            val existingWidget = repo.getWidget(widgetId)

            if (existingWidget == null) {
                Log.e(TAG, "Widget not found for update: $widgetId")
                return Result.failure()
            }

            // Parse existing calendar data
            val existingCalendarData = try {
                moshi.adapter(WidgetCalendarData::class.java).fromJson(existingWidget.data)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to parse existing calendar data", e)
                null
            }

            val currentDate = CalendarUtils.getCurrentDateInfo()
            val year = inputData.getInt(YEAR, currentDate.first)
            val month = inputData.getInt(MONTH, currentDate.second)

            // Update calendar data with new date info
            val updatedCalendarData = existingCalendarData?.copy(
                year = year,
                month = month,
                todayDay = if (year == currentDate.first && month == currentDate.second) {
                    currentDate.third
                } else null
            ) ?: WidgetCalendarData(
                year = year,
                month = month,
                todayDay = if (year == currentDate.first && month == currentDate.second) {
                    currentDate.third
                } else null
            )

            // Update widget data
            val updated = updateWidgetData(widgetId, updatedCalendarData)
            if (updated == null) {
                Log.e(TAG, "Failed to update existing widget data for widget: $widgetId")
                return Result.failure()
            }

            // Update widget UI
            val updateUISuccess = context.updateWidgetUI(widgetId, existingWidget.size)

            if (!updateUISuccess) {
                Log.e(TAG, "Failed to update existing widget UI for widget: $widgetId")
                return Result.failure()
            } else {
                Log.d(TAG, "Calendar widget $widgetId updated successfully")
                context.setWidgetSuccess(
                    glanceId = glanceId,
                    widgetSize = existingWidget.size,
                    widget = updated
                )
                return Result.success()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error in calendar update", e)
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

    private suspend fun updateWidgetData(widgetId: Int, calendarData: WidgetCalendarData): WidgetEntity? {
        return try {
            val repo = WidgetModelRepository.get(context)
            val widget = repo.getWidget(widgetId)

            // Update data and timestamp, preserve other fields
            val updatedWidget = widget?.copy(
                type = WidgetType.Calendar.Type1, // Default to Type1, can be modified based on requirements
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
}