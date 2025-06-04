package com.s4ltf1sh.glance_widgets

import android.appwidget.AppWidgetManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.lifecycle.lifecycleScope
import com.s4ltf1sh.glance_widgets.db.WidgetModelRepository
import com.s4ltf1sh.glance_widgets.ui.theme.GlancewidgetsTheme
import com.s4ltf1sh.glance_widgets.widget.core.BaseAppWidget
import com.s4ltf1sh.glance_widgets.widget.core.large.WidgetLarge
import com.s4ltf1sh.glance_widgets.widget.core.medium.WidgetMedium
import com.s4ltf1sh.glance_widgets.widget.core.small.WidgetSmall
import com.s4ltf1sh.glance_widgets.widget.model.WidgetType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val widgetId = intent.getIntExtra(
            BaseAppWidget.KEY_WIDGET_ID.name,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )

        val currentType = intent.getStringExtra(BaseAppWidget.KEY_WIDGET_TYPE.name)?.let {
            WidgetType.valueOf(it)
        } ?: WidgetType.NONE

        val currentSize = intent.getStringExtra(BaseAppWidget.KEY_WIDGET_SIZE.name) ?: "Dont know"

        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        setContent {
            if (currentType == WidgetType.NONE) {
                ConfigurationScreen(
                    widgetId = widgetId,
                    currentType = currentType,
                    onTypeSelected = { type ->
                        updateWidget(widgetId, type)
                        finish()
                    }
                )
            } else {
                Greeting(
                    name = "Widget ID: $widgetId, Type: $currentType, Size: $currentSize",
                )
            }
        }
    }

    private fun updateWidget(widgetId: Int, type: WidgetType) = lifecycleScope.launch {
        // Logic to update the widget with the new type
        // This could involve saving the type to a database or shared preferences
        // and then triggering a widget update
        val repo = WidgetModelRepository.get(applicationContext)
        val existingWidget = repo.getWidget(widgetId)

        if (existingWidget == null)
            return@launch

        val updatedWidget = existingWidget.copy(
            type = type,
            lastUpdated = System.currentTimeMillis()
        )

        repo.insertWidget(updatedWidget)
        updateSpecificWidget(widgetId)
    }

    private suspend fun updateSpecificWidget(widgetId: Int) {
        val glanceManager = GlanceAppWidgetManager(this)

        try {
            // Find the correct glance ID for this widget ID
            val smallGlanceIds = glanceManager.getGlanceIds(WidgetSmall::class.java)
            val mediumGlanceIds = glanceManager.getGlanceIds(WidgetMedium::class.java)
            val largeGlanceIds = glanceManager.getGlanceIds(WidgetLarge::class.java)

            // Try to update the widget by finding matching glance ID
            smallGlanceIds.forEach { glanceId ->
                if (glanceManager.getAppWidgetId(glanceId) == widgetId) {
                    WidgetSmall().update(this, glanceId)
                }
            }

            mediumGlanceIds.forEach { glanceId ->
                if (glanceManager.getAppWidgetId(glanceId) == widgetId) {
                    WidgetMedium().update(this, glanceId)
                }
            }

            largeGlanceIds.forEach { glanceId ->
                if (glanceManager.getAppWidgetId(glanceId) == widgetId) {
                    WidgetLarge().update(this, glanceId)
                }
            }

        } catch (e: Exception) {
            Log.e("ConfigurationActivity", "Error updating specific widget", e)
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GlancewidgetsTheme {
        Greeting("Android")
    }
}