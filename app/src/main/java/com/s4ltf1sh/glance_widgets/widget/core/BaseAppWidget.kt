package com.s4ltf1sh.glance_widgets.widget.core

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.work.WorkManager
import com.s4ltf1sh.glance_widgets.db.GlanceWidgetEntity
import com.s4ltf1sh.glance_widgets.db.GlanceWidgetRepository
import com.s4ltf1sh.glance_widgets.model.GlanceWidget
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetSize
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetType
import com.s4ltf1sh.glance_widgets.widget.widget.WidgetEmpty
import com.s4ltf1sh.glance_widgets.widget.widget.calendar.CalendarWidget
import com.s4ltf1sh.glance_widgets.widget.widget.clock.analog.ClockAnalogWidget
import com.s4ltf1sh.glance_widgets.widget.widget.clock.digital.ClockDigitalWidget
import com.s4ltf1sh.glance_widgets.widget.widget.photo.PhotoWidget
import com.s4ltf1sh.glance_widgets.widget.widget.quotes.QuotesWidget
import com.s4ltf1sh.glance_widgets.widget.widget.weather.WeatherWidget
import kotlinx.coroutines.launch

abstract class BaseAppWidget : GlanceAppWidget() {

    abstract val glanceWidgetSize: GlanceWidgetSize

    override val sizeMode = SizeMode.Exact

    // Use the new state definition
    override val stateDefinition = BaseWidgetStateDefinition

    companion object {
        val KEY_WIDGET_ID = ActionParameters.Key<Int>("widget_id")
        val KEY_WIDGET_TYPE = ActionParameters.Key<String>("widget_type")
        val KEY_WIDGET_SIZE = ActionParameters.Key<String>("widget_size")

        private const val WORKER_UNIQUE_NAME = "WorkerUniqueNameABTheme"

        fun getWidgetWorkerName(widgetId: Int): String {
            return "${WORKER_UNIQUE_NAME}_${widgetId}"
        }
    }

    override fun onCompositionError(
        context: Context,
        glanceId: GlanceId,
        appWidgetId: Int,
        throwable: Throwable
    ) {
        Log.e("BaseAppWidget", "Composition error for widget ID: $appWidgetId", throwable)
        super.onCompositionError(context, glanceId, appWidgetId, throwable)
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val modelRepo = GlanceWidgetRepository.get(context.applicationContext)
        val widgetId = GlanceAppWidgetManager(context).getAppWidgetId(id)

        provideContent {
            val appWidgetState = currentState<AppWidgetState>()
            val scope = rememberCoroutineScope()

            Log.d(
                "BaseAppWidget",
                "Providing widget with ID: $widgetId, Size: $glanceWidgetSize, State: $appWidgetState"
            )

            // Initialize widget if in Init state
            LaunchedEffect(appWidgetState) {
                Log.d("BaseAppWidget", "LaunchedEffect triggered for widget ID: $widgetId")
                if (appWidgetState == AppWidgetState.Init) {
                    scope.launch {
                        try {
                            val existingWidget = modelRepo.getWidget(widgetId)
                            if (existingWidget != null) {
                                // Widget exists in database, set to success
                                Log.d("BaseAppWidget", "Widget found in database: $existingWidget")
                                context.setWidgetSuccess(
                                    glanceId = id,
                                    glanceWidgetSize = glanceWidgetSize,
                                    widget = existingWidget
                                )
                            } else {
                                // Create new widget with default type
                                val defaultWidget = GlanceWidgetEntity(
                                    widgetId = widgetId,
                                    type = GlanceWidgetType.None,
                                    size = glanceWidgetSize
                                )
                                modelRepo.insertWidget(defaultWidget)

                                context.setWidgetEmpty(
                                    glanceId = id,
                                    glanceWidgetSize = glanceWidgetSize
                                )
                            }
                        } catch (e: Exception) {
                            Log.e("BaseAppWidget", "Error initializing widget", e)
                            context.setWidgetError(
                                glanceId = id,
                                glanceWidgetSize = glanceWidgetSize,
                                message = "Failed to initialize widget: ${e.message}",
                                throwable = e
                            )
                        }
                    }
                }
            }

            Content(appWidgetState, widgetId, context, id)
        }
    }

    override suspend fun onDelete(context: Context, glanceId: GlanceId) {
        super.onDelete(context, glanceId)
        val widgetId = GlanceAppWidgetManager(context).getAppWidgetId(glanceId)
        val modelRepo = GlanceWidgetRepository.get(context.applicationContext)

        try {
            modelRepo.deleteWidgetById(widgetId)
            workerCancel(context, widgetId)
        } catch (e: Exception) {
            Log.e("BaseAppWidget", "Error deleting widget", e)
        }
    }

    @Composable
    fun Content(
        appWidgetState: AppWidgetState,
        widgetId: Int,
        context: Context,
        glanceId: GlanceId
    ) {
        GlanceTheme {
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .appWidgetBackground()
                    .background(GlanceTheme.colors.background)
                    .cornerRadius(16.dp),
                contentAlignment = Alignment.Center
            ) {
                when (appWidgetState) {
                    AppWidgetState.Init -> ContentLoading()
                    AppWidgetState.Empty -> ContentEmpty(widgetId, context, glanceId)
                    is AppWidgetState.Success -> ContentSuccess(appWidgetState.glanceWidget, widgetId)
                    is AppWidgetState.Error -> ContentError(
                        appWidgetState.message,
                        widgetId,
                        context,
                        glanceId
                    )
                }
            }
        }
    }

    @Composable
    open fun ContentLoading() {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
            Text(
                text = "Initializing...",
                style = TextStyle(
                    color = GlanceTheme.colors.onSurface,
                    textAlign = TextAlign.Center
                ),
                modifier = GlanceModifier.padding(top = 8.dp)
            )
        }
    }

    @Composable
    open fun ContentEmpty(widgetId: Int, context: Context, glanceId: GlanceId) {
        WidgetEmpty(
            glanceWidget = GlanceWidgetEntity(
                widgetId = widgetId,
                type = GlanceWidgetType.None,
                size = glanceWidgetSize
            ).toWidget(),
            widgetId = widgetId
        )
    }

    @Composable
    open fun ContentSuccess(glanceWidget: GlanceWidget, widgetId: Int) {
        when (glanceWidget.type) {
            is GlanceWidgetType.Weather -> WeatherWidget(glanceWidget, widgetId)
            is GlanceWidgetType.Calendar -> CalendarWidget(glanceWidget, widgetId)
            is GlanceWidgetType.Clock.Digital -> ClockDigitalWidget(glanceWidget, widgetId)
            is GlanceWidgetType.Clock.Analog -> ClockAnalogWidget(glanceWidget, widgetId)
            GlanceWidgetType.Photo -> PhotoWidget(glanceWidget, widgetId)
            GlanceWidgetType.Quote -> QuotesWidget(glanceWidget, widgetId)
            else -> WidgetEmpty(glanceWidget, widgetId)
        }
    }

    @Composable
    open fun ContentError(
        errorMessage: String,
        widgetId: Int,
        context: Context,
        glanceId: GlanceId
    ) {
        val scope = rememberCoroutineScope()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = GlanceModifier.padding(16.dp)
        ) {
            Text(
                text = "Error",
                style = TextStyle(
                    color = ColorProvider(Color.Red),
                    textAlign = TextAlign.Center
                )
            )
            Text(
                text = errorMessage,
                style = TextStyle(
                    color = GlanceTheme.colors.onSurface,
                    textAlign = TextAlign.Center
                ),
                modifier = GlanceModifier.padding(top = 4.dp)
            )

            // Optional: Add retry functionality
            LaunchedEffect(Unit) {
                scope.launch {
                    // Auto retry after 5 seconds
                    kotlinx.coroutines.delay(5000)
                    context.refreshWidget(
                        glanceId = glanceId,
                        glanceWidgetSize = glanceWidgetSize
                    )
                }
            }
        }
    }

    open fun workerEnqueue(context: Context, size: DpSize, glanceId: GlanceId) {}

    open fun workerCancel(context: Context, widgetId: Int) {
        WorkManager.getInstance(context).cancelUniqueWork(getWidgetWorkerName(widgetId))
    }
}