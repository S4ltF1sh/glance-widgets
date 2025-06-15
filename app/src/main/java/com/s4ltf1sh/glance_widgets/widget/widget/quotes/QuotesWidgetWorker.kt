package com.s4ltf1sh.glance_widgets.widget.widget.quotes

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.s4ltf1sh.glance_widgets.db.WidgetModelRepository
import com.s4ltf1sh.glance_widgets.model.WidgetSize
import com.s4ltf1sh.glance_widgets.model.WidgetType
import com.s4ltf1sh.glance_widgets.model.quotes.WidgetQuoteData
import com.s4ltf1sh.glance_widgets.utils.downloadImageWithCoil
import com.s4ltf1sh.glance_widgets.widget.core.large.WidgetLarge
import com.s4ltf1sh.glance_widgets.widget.core.medium.WidgetMedium
import com.s4ltf1sh.glance_widgets.widget.core.small.WidgetSmall
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay

@HiltWorker
class QuotesWidgetWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {
    companion object {
        private const val WIDGET_ID = "widget_id"
        private const val WIDGET_TYPE = "widget_type"
        private const val WIDGET_SIZE = "widget_size"
        private const val IMAGE_URL = "image_url"

        private val uniqueWorkName = QuotesWidgetWorker::class.java.simpleName
        private val TAG = "QuotesWidgetWorker"

        // Retry configuration
        private const val MAX_RETRY_COUNT = 3
        private const val RETRY_DELAY_MS = 1000L

        fun enqueue(
            context: Context,
            widgetId: Int,
            type: WidgetType,
            widgetSize: WidgetSize,
            imageUrl: String
        ) {
            val workManager = WorkManager.getInstance(context)
            val request =
                OneTimeWorkRequestBuilder<QuotesWidgetWorker>().apply {
                    addTag(uniqueWorkName + widgetId.toString())
                    setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                    setInputData(
                        Data.Builder()
                            .putInt(WIDGET_ID, widgetId)
                            .putString(WIDGET_TYPE, type.name)
                            .putString(WIDGET_SIZE, widgetSize.name)
                            .putString(IMAGE_URL, imageUrl)
                            .build()
                    )
                }.build()

            workManager.enqueueUniqueWork(
                uniqueWorkName = uniqueWorkName,
                existingWorkPolicy = ExistingWorkPolicy.REPLACE,
                request = request
            )
        }

        fun cancel(context: Context, widgetId: Int) {
            WorkManager.getInstance(context).cancelUniqueWork(uniqueWorkName + widgetId.toString())
        }
    }

    override suspend fun doWork(): Result {
        // Extract input data
        val widgetId = inputData.getInt(WIDGET_ID, -1)
        if (widgetId == -1) {
            Log.e(TAG, "Invalid widget ID")
            return Result.failure()
        }

        val widgetSize = try {
            inputData.getString(WIDGET_SIZE)?.let { WidgetSize.valueOf(it) }
        } catch (e: Exception) {
            Log.e(TAG, "Invalid widget size", e)
            null
        }

        val imageUrl = inputData.getString(IMAGE_URL)

        // Validate inputs
        if (widgetSize == null || imageUrl.isNullOrEmpty()) {
            Log.e(TAG, "Missing required data - Size: $widgetSize, URL: $imageUrl")
            return Result.failure()
        }

        // Start download process
        return downloadAndUpdateWidget(widgetId, widgetSize, imageUrl)
    }

    private suspend fun downloadAndUpdateWidget(
        widgetId: Int,
        widgetSize: WidgetSize,
        imageUrl: String
    ): Result {
        try {
            // Get appropriate dimensions for widget size
            // Download image with retry logic
            val imagePath = downloadImageWithRetry(url = imageUrl)

            if (imagePath.isEmpty()) {
                Log.e(TAG, "Failed to download image after retries for widget: $widgetId")
                return Result.failure()
            }

            Log.d(TAG, "Image downloaded successfully: $imagePath")

            // Update widget data in repository
            val updated = updateWidgetData(widgetId, imagePath)
            if (!updated) {
                Log.e(TAG, "Failed to update widget data for widget: $widgetId")
                return Result.failure()
            }

            // Update widget UI
            val updateUISuccess = updateWidgetUI(widgetId, widgetSize)

            if (!updateUISuccess) {
                Log.e(TAG, "Failed to update widget UI for widget: $widgetId")
                return Result.failure()
            } else {
                Log.d(TAG, "Widget $widgetId updated successfully")
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

    private suspend fun updateWidgetData(widgetId: Int, imagePath: String): Boolean {
        return try {
            val repo = WidgetModelRepository.get(context)
            val widget = repo.getWidget(widgetId)
            val quoteData = WidgetQuoteData(imagePath = imagePath,)

            // Only update data and timestamp, preserve other fields
            val updatedWidget = widget?.copy(
                type = WidgetType.QUOTES,
                data = Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .build()
                    .adapter(WidgetQuoteData::class.java)
                    .toJson(quoteData),
                lastUpdated = System.currentTimeMillis()
            )

            if (updatedWidget == null) {
                Log.e(TAG, "Widget not found for ID: $widgetId")
                false
            } else {
                Log.d(TAG, "Updating widget data for ID: $widgetId with image: $imagePath")
                repo.insertWidget(updatedWidget)
                true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating widget data", e)
            false
        }
    }

    private suspend fun updateWidgetUI(widgetId: Int, widgetSize: WidgetSize): Boolean {
        return try {
            val glanceManager = GlanceAppWidgetManager(context)
            val glanceId = glanceManager.getGlanceIdBy(widgetId)

            when (widgetSize) {
                WidgetSize.SMALL -> WidgetSmall().update(context, glanceId)
                WidgetSize.MEDIUM -> WidgetMedium().update(context, glanceId)
                WidgetSize.LARGE -> WidgetLarge().update(context, glanceId)
            }

            true
        } catch (e: Exception) {
            Log.e(TAG, "Error updating widget UI", e)
            false
        }
    }
}