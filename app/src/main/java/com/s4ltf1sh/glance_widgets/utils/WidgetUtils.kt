package com.s4ltf1sh.glance_widgets.utils

import android.content.Context
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetSize
import com.s4ltf1sh.glance_widgets.widget.core.large.WidgetLarge
import com.s4ltf1sh.glance_widgets.widget.core.medium.WidgetMedium
import com.s4ltf1sh.glance_widgets.widget.core.small.WidgetSmall

suspend fun Context.updateWidgetUI(widgetId: Int, glanceWidgetSize: GlanceWidgetSize): Boolean {
    return try {
        val glanceManager = GlanceAppWidgetManager(applicationContext)
        val glanceId = glanceManager.getGlanceIdBy(widgetId)

        Log.d("updateWidgetUI", "Updating widget with ID: $widgetId, Size: $glanceWidgetSize")

        when (glanceWidgetSize) {
            GlanceWidgetSize.SMALL -> WidgetSmall().update(applicationContext, glanceId)
            GlanceWidgetSize.MEDIUM -> WidgetMedium().update(applicationContext, glanceId)
            GlanceWidgetSize.LARGE -> WidgetLarge().update(applicationContext, glanceId)
        }

        true
    } catch (e: Exception) {
        Log.e("updateWidgetUI", "Error updating widget UI", e)
        false
    }
}

suspend fun Context.updateWidgetUI(glanceId: GlanceId, glanceWidgetSize: GlanceWidgetSize): Boolean {
    return try {
        Log.d("updateWidgetUI", "Updating widget with GlanceID: $glanceId, Size: $glanceWidgetSize")

        when (glanceWidgetSize) {
            GlanceWidgetSize.SMALL -> WidgetSmall().update(applicationContext, glanceId)
            GlanceWidgetSize.MEDIUM -> WidgetMedium().update(applicationContext, glanceId)
            GlanceWidgetSize.LARGE -> WidgetLarge().update(applicationContext, glanceId)
        }

        true
    } catch (e: Exception) {
        Log.e("updateWidgetUI", "Error updating widget UI", e)
        false
    }
}