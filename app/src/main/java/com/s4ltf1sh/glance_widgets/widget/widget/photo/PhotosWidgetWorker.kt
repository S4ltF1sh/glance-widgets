package com.s4ltf1sh.glance_widgets.widget.widget.photo

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.s4ltf1sh.glance_widgets.db.GlanceWidgetRepository
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetSize
import com.s4ltf1sh.glance_widgets.model.photo.WidgetPhotoData
import com.s4ltf1sh.glance_widgets.utils.updateWidgetUI
import com.s4ltf1sh.glance_widgets.widget.core.BaseAppWidget
import com.s4ltf1sh.glance_widgets.widget.core.setWidgetEmpty
import com.s4ltf1sh.glance_widgets.widget.core.setWidgetError
import com.s4ltf1sh.glance_widgets.widget.core.setWidgetSuccess
import com.squareup.moshi.Moshi
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class PhotosWidgetWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    val moshi: Moshi
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val WIDGET_ID = "widget_id"
        private const val WIDGET_SIZE = "widget_size"

        private const val TAG = "PhotosWidgetWorker"

        fun enqueue(
            context: Context,
            widgetId: Int,
            glanceWidgetSize: GlanceWidgetSize,
            repeatTimeInMinutes: Long = 15,
        ) {
            Log.d(TAG, "Enqueuing periodic work for widget ID: $widgetId, Size: $glanceWidgetSize")
            val workManager = WorkManager.getInstance(context)
            val widgetWorkerName = BaseAppWidget.getWidgetWorkerName(widgetId)
            val request = PeriodicWorkRequest.Builder(
                workerClass = PhotosWidgetWorker::class.java,
                repeatInterval = repeatTimeInMinutes,
                repeatIntervalTimeUnit = java.util.concurrent.TimeUnit.MINUTES
            ).apply {
                addTag(widgetWorkerName)
                setInputData(
                    Data.Builder()
                        .putInt(WIDGET_ID, widgetId)
                        .putString(WIDGET_SIZE, glanceWidgetSize.name)
                        .build()
                )
            }.build()

            workManager.enqueueUniquePeriodicWork(
                uniqueWorkName = widgetWorkerName,
                existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.REPLACE,
                request = request
            )
        }

        fun enqueueOnce(
            context: Context,
            widgetId: Int,
            glanceWidgetSize: GlanceWidgetSize
        ) {
            val workManager = WorkManager.getInstance(context)
            val widgetWorkerName = BaseAppWidget.getWidgetWorkerName(widgetId) + "_once"
            val request = OneTimeWorkRequestBuilder<PhotosWidgetWorker>().apply {
                addTag(widgetWorkerName)
                setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                setInputData(
                    Data.Builder()
                        .putInt(WIDGET_ID, widgetId)
                        .putString(WIDGET_SIZE, glanceWidgetSize.name)
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

        val glanceWidgetSize = try {
            inputData.getString(WIDGET_SIZE)?.let { GlanceWidgetSize.valueOf(it) }
        } catch (e: Exception) {
            Log.e(TAG, "Invalid widget size", e)
            null
        }

        if (glanceWidgetSize == null) {
            Log.e(TAG, "Missing required data - Size: $glanceWidgetSize")
            context.setWidgetError(
                glanceId = glanceId,
                glanceWidgetSize = GlanceWidgetSize.SMALL,
                message = "Invalid widget size",
                throwable = IllegalArgumentException("Widget size is missing")
            )
            return Result.failure()
        }

        val repo = GlanceWidgetRepository.get(context)
        val widget = repo.getWidget(widgetId)

        if (widget == null) {
            Log.e(TAG, "Widget not found for ID: $widgetId")
            context.setWidgetError(
                glanceId = glanceId,
                glanceWidgetSize = glanceWidgetSize,
                message = "Widget not found",
                throwable = NullPointerException("Widget with ID $widgetId does not exist")
            )
            return Result.failure()
        }

        val photoDatas = moshi.adapter(WidgetPhotoData::class.java)
            .fromJson(widget.data)

        if (photoDatas == null || photoDatas.photoPaths.isEmpty()) {
            Log.e(TAG, "No photo data found for widget ID: $widgetId")
            context.setWidgetEmpty(
                glanceId = glanceId,
                glanceWidgetSize = glanceWidgetSize
            )
            return Result.failure()
        }
        val nextIndex = (photoDatas.index + 1) % photoDatas.photoPaths.size
        val updatedData = photoDatas.copy(index = nextIndex)
        val updatedWidget = widget.copy(
            data = moshi.adapter(WidgetPhotoData::class.java).toJson(updatedData)
        )

        Log.d(TAG, "Updating widget data for ID: $widgetId with index: $nextIndex")

        val updateWidgetSuccess = repo.updateWidget(updatedWidget) > 0

        if (!updateWidgetSuccess) {
            Log.e(TAG, "Failed to update widget data for widget: $widgetId")
            context.setWidgetError(
                glanceId = glanceId,
                glanceWidgetSize = glanceWidgetSize,
                message = "Failed to update widget data",
                throwable = Exception("Update failed for widget ID $widgetId")
            )
            return Result.failure()
        }

        val updateUISuccess = context.updateWidgetUI(widgetId, glanceWidgetSize)

        if (!updateUISuccess) {
            Log.e(TAG, "Failed to update widget UI for widget: $widgetId")
            context.setWidgetError(
                glanceId = glanceId,
                glanceWidgetSize = glanceWidgetSize,
                message = "Failed to update widget UI",
                throwable = Exception("UI update failed for widget ID $widgetId")
            )
            return Result.failure()
        } else {
            Log.d(TAG, "Widget $widgetId updated successfully")
            context.setWidgetSuccess(
                glanceId = glanceId,
                glanceWidgetSize = glanceWidgetSize,
                widget = updatedWidget
            )
            return Result.success()
        }
    }
}