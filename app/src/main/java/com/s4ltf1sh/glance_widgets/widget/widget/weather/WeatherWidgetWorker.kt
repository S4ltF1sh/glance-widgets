package com.s4ltf1sh.glance_widgets.widget.widget.weather

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import com.s4ltf1sh.glance_widgets.db.GlanceWidgetRepository
import com.s4ltf1sh.glance_widgets.di.GetForecastUseCase
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetSize
import com.s4ltf1sh.glance_widgets.widget.core.BaseAppWidget
import com.s4ltf1sh.glance_widgets.widget.core.refreshWidget
import com.s4ltf1sh.glance_widgets.widget.core.setWidgetError
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class WeatherWidgetWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted val params: androidx.work.WorkerParameters,
    val getForecastUseCase: GetForecastUseCase
) : CoroutineWorker(context, params) {

    companion object {
        private const val WIDGET_ID = "widget_id"
        private const val WIDGET_SIZE = "widget_size"

        private const val TAG = "WeatherWidgetWorker"

        // Retry configuration
        private const val MAX_RETRY_COUNT = 3
        private const val RETRY_DELAY_MS = 1000L

        fun enqueue(
            context: Context,
            widgetId: Int,
            widgetSize: GlanceWidgetSize,
            repeatTimeInMinutes: Long = 60
        ) {
            Log.d(TAG, "Enqueuing periodic work for widget ID: $widgetId")
            val workManager = WorkManager.getInstance(context = context)
            val widgetWorkerName = BaseAppWidget.getWidgetWorkerName(widgetId)

            val request = androidx.work.PeriodicWorkRequest.Builder(
                workerClass = WeatherWidgetWorker::class.java,
                repeatInterval = repeatTimeInMinutes,
                repeatIntervalTimeUnit = java.util.concurrent.TimeUnit.MINUTES
            ).apply {
                addTag(widgetWorkerName)
                setInputData(
                    androidx.work.Data.Builder()
                        .putInt(WIDGET_ID, widgetId)
                        .putString(WIDGET_SIZE, widgetSize.name)
                        .build()
                )
            }.build()

            workManager.enqueueUniquePeriodicWork(
                widgetWorkerName,
                androidx.work.ExistingPeriodicWorkPolicy.REPLACE,
                request
            )
        }
    }

    override suspend fun doWork(): Result {
        // Implement the logic to update the weather widget here
        // This could involve fetching weather data from an API and updating the widget's UI
        val widgetId = inputData.getInt(WIDGET_ID, -1)

        if (widgetId == -1) {
            Log.e(TAG, "Invalid widget ID")
            return Result.failure() // Return failure if widget ID is invalid
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

        context.refreshWidget(
            glanceId = glanceId,
            glanceWidgetSize = glanceWidgetSize
        )

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

        //TODO: Fetch weather data using the GetForecastUseCase

        return Result.success() // Return success or failure based on the operation
    }
}