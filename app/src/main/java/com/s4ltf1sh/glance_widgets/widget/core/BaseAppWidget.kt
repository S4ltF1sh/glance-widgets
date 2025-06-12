package com.s4ltf1sh.glance_widgets.widget.core

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.provideContent
import com.s4ltf1sh.glance_widgets.db.WidgetEntity
import com.s4ltf1sh.glance_widgets.db.WidgetModelRepository
import com.s4ltf1sh.glance_widgets.model.WidgetSize
import com.s4ltf1sh.glance_widgets.model.WidgetType
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
                WidgetContent(widget = it, widgetId = widgetId)
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
    }

    @Composable
    abstract fun WidgetContent(widget: WidgetEntity, widgetId: Int)
}