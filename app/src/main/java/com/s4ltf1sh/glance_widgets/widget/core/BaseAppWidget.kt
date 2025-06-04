package com.s4ltf1sh.glance_widgets.widget.core

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.provideContent
import com.s4ltf1sh.glance_widgets.db.WidgetEntity
import com.s4ltf1sh.glance_widgets.db.WidgetModelRepository
import com.s4ltf1sh.glance_widgets.widget.model.WidgetSize
import com.s4ltf1sh.glance_widgets.widget.model.WidgetType

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

        val widget = modelRepo.getWidget(widgetId) ?: run {
            // Create default widget if not exists
            val defaultWidget = WidgetEntity(
                widgetId = widgetId,
                type = WidgetType.NONE, // Default type
                size = widgetSize
            )
            modelRepo.insertWidget(defaultWidget)
            defaultWidget
        }

        provideContent {
            WidgetContent(widget = widget, widgetId = widgetId)
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