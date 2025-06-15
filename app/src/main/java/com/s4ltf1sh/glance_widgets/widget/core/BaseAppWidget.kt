package com.s4ltf1sh.glance_widgets.widget.core

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import com.s4ltf1sh.glance_widgets.db.WidgetEntity
import com.s4ltf1sh.glance_widgets.db.WidgetModelRepository
import com.s4ltf1sh.glance_widgets.model.WidgetSize
import com.s4ltf1sh.glance_widgets.model.WidgetType
import com.s4ltf1sh.glance_widgets.widget.widget.WidgetEmpty
import com.s4ltf1sh.glance_widgets.widget.widget.calendar.CalendarWidget
import com.s4ltf1sh.glance_widgets.widget.widget.photo.PhotoWidget
import com.s4ltf1sh.glance_widgets.widget.widget.quotes.QuotesWidget
import com.s4ltf1sh.glance_widgets.widget.widget.weather.WeatherWidget
import kotlinx.coroutines.launch

abstract class BaseAppWidget : GlanceAppWidget() {

    abstract val widgetSize: WidgetSize

    companion object {
        val KEY_WIDGET_ID = ActionParameters.Key<Int>("widget_id")
        val KEY_WIDGET_TYPE = ActionParameters.Key<String>("widget_type")
        val KEY_WIDGET_SIZE = ActionParameters.Key<String>("widget_size")
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val modelRepo = WidgetModelRepository.get(context.applicationContext)
        val widgetId = GlanceAppWidgetManager(context).getAppWidgetId(id)

        provideContent {
            val widgetState = modelRepo.getWidgetFlow(widgetId).collectAsState(null)
            val scope = rememberCoroutineScope()

            widgetState.value?.let {
                Content(it, widgetId)
            } ?: run {
                val defaultWidget = WidgetEntity(
                    widgetId = widgetId,
                    type = WidgetType.NONE, // Default type
                    size = widgetSize
                )

                scope.launch {
                    modelRepo.insertWidget(defaultWidget)
                }
            }
        }
    }

    override suspend fun onDelete(context: Context, glanceId: GlanceId) {
        super.onDelete(context, glanceId)
        val widgetId = GlanceAppWidgetManager(context).getAppWidgetId(glanceId)
        val modelRepo = WidgetModelRepository.get(context.applicationContext)
        modelRepo.deleteWidgetById(widgetId)
        workerCancel(context, widgetId)
    }

    @Composable
    fun Content(widget: WidgetEntity, widgetId: Int) {
        GlanceTheme {
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .appWidgetBackground()
                    .background(GlanceTheme.colors.background)
                    .cornerRadius(16.dp),
                contentAlignment = Alignment.Center
            ) {
                ContentSuccess(widget, widgetId)
            }
        }
    }

    @Composable
    open fun ContentLoading() {
        CircularProgressIndicator()
    }

    @Composable
    open fun ContentSuccess(widget: WidgetEntity, widgetId: Int) {
        when (widget.type) {
            WidgetType.WEATHER -> WeatherWidget(widget, widgetId)
            WidgetType.CALENDAR -> CalendarWidget(widget, widgetId)
            WidgetType.PHOTO -> PhotoWidget(widget, widgetId)
            WidgetType.QUOTES -> QuotesWidget(widget, widgetId)
            else -> WidgetEmpty(widget, widgetId)
        }
    }

    @Composable
    open fun ContentError() {

    }

    abstract fun workerEnqueue(context: Context, size: DpSize, glanceId: GlanceId)

    abstract fun workerCancel(context: Context, widgetId: Int)
}