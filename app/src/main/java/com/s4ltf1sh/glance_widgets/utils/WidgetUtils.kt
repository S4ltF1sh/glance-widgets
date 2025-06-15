package com.s4ltf1sh.glance_widgets.utils

import android.content.Context
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import com.s4ltf1sh.glance_widgets.model.WidgetSize
import com.s4ltf1sh.glance_widgets.widget.core.large.WidgetLarge
import com.s4ltf1sh.glance_widgets.widget.core.medium.WidgetMedium
import com.s4ltf1sh.glance_widgets.widget.core.small.WidgetSmall

suspend fun Context.updateWidgetUI(widgetId: Int, widgetSize: WidgetSize): Boolean {
    return try {
        val glanceManager = GlanceAppWidgetManager(applicationContext)
        val glanceId = glanceManager.getGlanceIdBy(widgetId)

        Log.d("updateWidgetUI", "Updating widget with ID: $widgetId, Size: $widgetSize")

        when (widgetSize) {
            WidgetSize.SMALL -> WidgetSmall().update(applicationContext, glanceId)
            WidgetSize.MEDIUM -> WidgetMedium().update(applicationContext, glanceId)
            WidgetSize.LARGE -> WidgetLarge().update(applicationContext, glanceId)
        }

        true
    } catch (e: Exception) {
        Log.e("updateWidgetUI", "Error updating widget UI", e)
        false
    }
}

suspend fun Context.updateWidgetUI(glanceId: GlanceId, widgetSize: WidgetSize): Boolean {
    return try {
        Log.d("updateWidgetUI", "Updating widget with GlanceID: $glanceId, Size: $widgetSize")

        when (widgetSize) {
            WidgetSize.SMALL -> WidgetSmall().update(applicationContext, glanceId)
            WidgetSize.MEDIUM -> WidgetMedium().update(applicationContext, glanceId)
            WidgetSize.LARGE -> WidgetLarge().update(applicationContext, glanceId)
        }

        true
    } catch (e: Exception) {
        Log.e("updateWidgetUI", "Error updating widget UI", e)
        false
    }
}