package com.s4ltf1sh.glance_widgets.widget.core

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.s4ltf1sh.glance_widgets.model.WidgetSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.util.Calendar

abstract class BaseWidgetReceiver : GlanceAppWidgetReceiver() {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    abstract val widgetSize: WidgetSize

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)

    }
}