package com.s4ltf1sh.glance_widgets.widget.core

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.LocalGlanceId
import androidx.glance.LocalSize
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.state.GlanceStateDefinition
import com.s4ltf1sh.glance_widgets.db.WidgetEntity
import com.s4ltf1sh.glance_widgets.db.WidgetModelRepository
import com.s4ltf1sh.glance_widgets.model.WidgetSize
import com.s4ltf1sh.glance_widgets.model.WidgetState
import com.s4ltf1sh.glance_widgets.model.WidgetType
import com.s4ltf1sh.glance_widgets.widget.widget.WidgetStateDefinition
import kotlinx.coroutines.launch

abstract class BaseAppWidget : GlanceAppWidget() {

    abstract val widgetSize: WidgetSize

    companion object {
        val KEY_WIDGET_ID = ActionParameters.Key<Int>("widget_id")
        val KEY_WIDGET_TYPE = ActionParameters.Key<String>("widget_type")
        val KEY_WIDGET_SIZE = ActionParameters.Key<String>("widget_size")
    }

    override val stateDefinition: GlanceStateDefinition<*>?
        get() = WidgetStateDefinition

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
        workerCancel(context, glanceId)
    }

    @Composable
    fun Content(widget: WidgetEntity, widgetId: Int) {
        val size = LocalSize.current
        val context = LocalContext.current
        val glanceId = LocalGlanceId.current
        val widgetState = currentState<WidgetState>()

        GlanceTheme {
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .appWidgetBackground()
                    .background(GlanceTheme.colors.background)
                    .cornerRadius(16.dp),
                contentAlignment = Alignment.Center
            ) {
                when (widgetState) {
                    WidgetState.Error -> ContentError()
                    WidgetState.Loading -> ContentLoading()
                    is WidgetState.Success -> ContentSuccess(widget, widgetId)
                }

                LaunchedEffect(Unit) {
                    workerEnqueue(context, size, glanceId)
                }
            }
        }
    }

    @Composable
    open fun ContentLoading() {
        CircularProgressIndicator()
    }

    @Composable
    abstract fun ContentSuccess(widget: WidgetEntity, widgetId: Int)

    @Composable
    open fun ContentError() {

    }

    abstract fun workerEnqueue(context: Context, size: DpSize, glanceId: GlanceId)

    abstract fun workerCancel(context: Context, glanceId: GlanceId)
}