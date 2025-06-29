package com.s4ltf1sh.glance_widgets.widget.widget.quotes

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
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetSize
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetType
import com.s4ltf1sh.glance_widgets.model.quotes.WidgetQuoteData
import com.s4ltf1sh.glance_widgets.utils.downloadImageWithCoil
import com.s4ltf1sh.glance_widgets.utils.updateWidgetUI
import com.s4ltf1sh.glance_widgets.widget.core.BaseAppWidget
import com.s4ltf1sh.glance_widgets.widget.core.setWidgetEmpty
import com.s4ltf1sh.glance_widgets.widget.core.setWidgetError
import com.s4ltf1sh.glance_widgets.widget.core.setWidgetSuccess
import com.squareup.moshi.Moshi
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay

@HiltWorker
class QuotesWidgetWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    val moshi: Moshi
) : CoroutineWorker(context, workerParams) {
    companion object {
        private const val WIDGET_ID = "widget_id"
        private const val WIDGET_TYPE = "widget_type"
        private const val WIDGET_SIZE = "widget_size"
        private const val IMAGE_URL = "image_url"

        private const val TAG = "QuotesWidgetWorker"

        // Retry configuration
        private const val MAX_RETRY_COUNT = 3
        private const val RETRY_DELAY_MS = 1000L

        fun enqueue(
            context: Context,
            widgetId: Int,
            type: GlanceWidgetType,
            glanceWidgetSize: GlanceWidgetSize,
            imageUrl: String
        ) {
            Log.d(TAG, "Enqueuing work for widget ID: $widgetId, Type: $type, Size: $glanceWidgetSize, URL: $imageUrl")
            val workManager = WorkManager.getInstance(context)
            val widgetWorkerName = BaseAppWidget.getWidgetWorkerName(widgetId)
            val request =
                OneTimeWorkRequestBuilder<QuotesWidgetWorker>().apply {
                    addTag(widgetWorkerName)
                    setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                    setInputData(
                        Data.Builder()
                            .putInt(WIDGET_ID, widgetId)
                            .putString(WIDGET_TYPE, type.typeId)
                            .putString(WIDGET_SIZE, glanceWidgetSize.name)
                            .putString(IMAGE_URL, imageUrl)
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
        // Extract input data
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

        val imageUrl = inputData.getString(IMAGE_URL)

        // Validate inputs
        if (glanceWidgetSize == null || imageUrl.isNullOrEmpty()) {
            Log.e(TAG, "Missing required data - Size: $glanceWidgetSize, URL: $imageUrl")
            context.setWidgetError(
                glanceId = glanceId,
                glanceWidgetSize = glanceWidgetSize ?: GlanceWidgetSize.SMALL,
                message = "Invalid input data",
                throwable = IllegalArgumentException("Widget size or image URL is missing")
            )
            return Result.failure()
        }

        // Start download process
        return downloadAndUpdateWidget(widgetId, glanceId, glanceWidgetSize, imageUrl)
    }

    private suspend fun downloadAndUpdateWidget(
        widgetId: Int,
        glanceId: GlanceId,
        glanceWidgetSize: GlanceWidgetSize,
        imageUrl: String
    ): Result {
        try {
            // Get appropriate dimensions for widget size
            // Download image with retry logic
            val imagePath = downloadImageWithRetry(url = imageUrl)

            if (imagePath.isEmpty()) {
                Log.e(TAG, "Failed to download image after retries for widget: $widgetId")
                context.setWidgetEmpty(
                    glanceId = glanceId,
                    glanceWidgetSize = glanceWidgetSize
                )
                return Result.failure()
            }

            Log.d(TAG, "Image downloaded successfully: $imagePath")

            // Update widget data in repository
            val updated = updateWidgetData(widgetId, imagePath)
            if (updated == null) {
                Log.e(TAG, "Failed to update widget data for widget: $widgetId")
                context.setWidgetError(
                    glanceId = glanceId,
                    glanceWidgetSize = glanceWidgetSize,
                    message = "Failed to update widget data",
                    throwable = NullPointerException("Widget with ID $widgetId does not exist")
                )
                return Result.failure()
            }

            // Update widget UI
            val updateUISuccess = context.updateWidgetUI(widgetId, glanceWidgetSize)

            if (!updateUISuccess) {
                Log.e(TAG, "Failed to update widget UI for widget: $widgetId")
                context.setWidgetError(
                    glanceId = glanceId,
                    glanceWidgetSize = glanceWidgetSize,
                    message = "Failed to update widget UI",
                    throwable = Exception("Update UI failed for widget ID $widgetId")
                )
                return Result.failure()
            } else {
                Log.d(TAG, "Widget $widgetId updated successfully")
                context.setWidgetSuccess(
                    glanceId = glanceId,
                    glanceWidgetSize = glanceWidgetSize,
                    widget = updated
                )
                return Result.success()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error in worker", e)

            // Retry if under limit
            if (runAttemptCount < MAX_RETRY_COUNT) {
                Log.d(TAG, "Retrying... Attempt ${runAttemptCount + 1}/$MAX_RETRY_COUNT")
                return Result.retry()
            }

            return Result.failure()
        }
    }

    private suspend fun downloadImageWithRetry(url: String): String {
        var lastError: Exception? = null

        repeat(MAX_RETRY_COUNT) { attempt ->
            try {
                val imagePath = context.downloadImageWithCoil(
                    url = url,
                    force = true // Always force download for quotes
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
        return ""
    }

    private suspend fun updateWidgetData(widgetId: Int, imagePath: String): GlanceWidgetEntity? {
        return try {
            val repo = GlanceWidgetRepository.get(context)
            val widget = repo.getWidget(widgetId)
            val quoteData = WidgetQuoteData(imagePath = imagePath)

            // Only update data and timestamp, preserve other fields
            val updatedWidget = widget?.copy(
                type = GlanceWidgetType.Quote,
                data = moshi
                    .adapter(WidgetQuoteData::class.java)
                    .toJson(quoteData),
                lastUpdated = System.currentTimeMillis()
            )

            if (updatedWidget == null) {
                Log.e(TAG, "Widget not found for ID: $widgetId")
                null
            } else {
                Log.d(TAG, "Updating widget data for ID: $widgetId with image: $imagePath")
                repo.insertWidget(updatedWidget)
                updatedWidget
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating widget data", e)
            null
        }
    }
}