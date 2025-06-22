package com.s4ltf1sh.glance_widgets.widget.core

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.s4ltf1sh.glance_widgets.db.WidgetModelRepository
import com.s4ltf1sh.glance_widgets.model.WidgetSize
import com.s4ltf1sh.glance_widgets.model.WidgetType
import com.s4ltf1sh.glance_widgets.utils.updateWidgetUI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

abstract class BaseWidgetReceiver : GlanceAppWidgetReceiver() {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    abstract val widgetSize: WidgetSize

//    override fun onReceive(context: Context, intent: Intent) {
//        super.onReceive(context, intent)
//        val action = intent.action
//
//        if (action == Intent.ACTION_TIME_CHANGED || action == Intent.ACTION_TIMEZONE_CHANGED) {
//            scope.launch {
//                val appWidgetManager = GlanceAppWidgetManager(context)
//                val widgetIds =
//                    WidgetModelRepository.get(context.applicationContext).getWidgetsByType(
//                        WidgetType.Clock.Analog
//                    ).map { it.widgetId }
//
//                val glanceIds = widgetIds.map { widgetId ->
//                    appWidgetManager.getGlanceIdBy(widgetId)
//                }
//
//                glanceIds.forEach { glanceId ->
//                    context.updateWidgetUI(w)
//                }
//            }
//
//        }
//    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
    }
}