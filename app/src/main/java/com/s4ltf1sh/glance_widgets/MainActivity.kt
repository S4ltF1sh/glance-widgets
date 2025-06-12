package com.s4ltf1sh.glance_widgets

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.FileProvider.getUriForFile
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import coil3.imageLoader
import coil3.memory.MemoryCache
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import com.s4ltf1sh.glance_widgets.db.WidgetModelRepository
import com.s4ltf1sh.glance_widgets.db.quote.QuoteEntity
import com.s4ltf1sh.glance_widgets.model.WidgetSize
import com.s4ltf1sh.glance_widgets.model.WidgetType
import com.s4ltf1sh.glance_widgets.model.quotes.WidgetQuoteData
import com.s4ltf1sh.glance_widgets.ui.theme.GlancewidgetsTheme
import com.s4ltf1sh.glance_widgets.widget.QuoteSelectionScreen
import com.s4ltf1sh.glance_widgets.widget.core.BaseAppWidget
import com.s4ltf1sh.glance_widgets.widget.core.large.WidgetLarge
import com.s4ltf1sh.glance_widgets.widget.core.medium.WidgetMedium
import com.s4ltf1sh.glance_widgets.widget.core.small.WidgetSmall
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val widgetId = intent.getIntExtra(
            BaseAppWidget.KEY_WIDGET_ID.name,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )

        val currentType = intent.getStringExtra(BaseAppWidget.KEY_WIDGET_TYPE.name)?.let {
            WidgetType.valueOf(it)
        } ?: WidgetType.NONE

        val currentSize = intent.getStringExtra(BaseAppWidget.KEY_WIDGET_SIZE.name)?.let {
            WidgetSize.valueOf(it)
        } ?: WidgetSize.SMALL

        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        mainViewModel.getQuotesBySize(currentSize)

        setContent {
            val quotes = mainViewModel.quotes.collectAsStateWithLifecycle()

            GlancewidgetsTheme {
                var screenState by remember { mutableStateOf(ScreenState.TYPE_SELECTION) }

                when (screenState) {
                    ScreenState.TYPE_SELECTION -> {
                        if (currentType == WidgetType.NONE) {
                            ConfigurationScreen(
                                widgetId = widgetId,
                                currentType = currentType,
                                onTypeSelected = { type ->
                                    if (type == WidgetType.QUOTES) {
                                        screenState = ScreenState.QUOTE_SELECTION
                                    } else {
                                        updateWidget(widgetId, type, widgetSize = currentSize)
                                        finish()
                                    }
                                }
                            )
                        } else {
                            // Widget already configured
                            Greeting(
                                name = "Widget ID: $widgetId, Type: $currentType, Size: $currentSize",
                            )
                        }
                    }

                    ScreenState.QUOTE_SELECTION -> {
                        QuoteSelectionScreen(
                            widgetSize = currentSize,
                            quotes = quotes.value,
                            onQuoteSelected = { quote ->
                                updateQuotesWidget(widgetId, currentSize, quote)
                            }
                        )
                    }
                }
            }
        }

        // Initialize sample quotes if needed
        lifecycleScope.launch {
            initializeSampleQuotes()
        }
    }

    private fun updateWidget(
        widgetId: Int,
        type: WidgetType,
        widgetSize: WidgetSize,
        data: String = ""
    ) = lifecycleScope.launch {
        val repo = WidgetModelRepository.get(applicationContext)
        val existingWidget = repo.getWidget(widgetId)

        if (existingWidget == null) {
            Log.e("MainActivity", "Widget with ID $widgetId not found")
            return@launch
        }

        Log.d(
            "MainActivity",
            "Updating widget with ID $widgetId, Type: $type, Size: $widgetSize, Data: $data"
        )

        val updatedWidget = existingWidget.copy(
            type = type,
            data = data,
            lastUpdated = System.currentTimeMillis()
        )

        repo.insertWidget(updatedWidget)
        updateSpecificWidget(widgetId, widgetSize)
    }

    private suspend fun updateSpecificWidget(widgetId: Int, widgetSize: WidgetSize) {
        val glanceManager = GlanceAppWidgetManager(this)
        val glanceId = glanceManager.getGlanceIdBy(widgetId)
        try {
            when (widgetSize) {
                WidgetSize.SMALL -> {
                    WidgetSmall().update(this, glanceId)
                }

                WidgetSize.MEDIUM -> {
                    WidgetMedium().update(this, glanceId)
                }

                WidgetSize.LARGE -> {
                    WidgetLarge().update(this, glanceId)
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error updating specific widget", e)
        }
    }

    private fun updateQuotesWidget(
        widgetId: Int,
        widgetSize: WidgetSize,
        quote: QuoteEntity
    ) = lifecycleScope.launch {
        val quoteData = WidgetQuoteData(
            quoteId = quote.id,
            imageUrl = quote.imageUrl,
        )

        updateWidget(
            widgetId = widgetId,
            type = WidgetType.QUOTES,
            widgetSize = widgetSize,
            data = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
                .adapter(WidgetQuoteData::class.java)
                .toJson(quoteData)
        )

        finish()
    }

    private fun initializeSampleQuotes() {
        // Add sample quotes - replace with your actual quote images
        val sampleQuotes = listOf(
            // Set 1 - Motivational
            QuoteEntity(
                size = WidgetSize.SMALL,
                imageUrl = "https://picsum.photos/400"
            ),
            QuoteEntity(
                size = WidgetSize.MEDIUM,
                imageUrl = "https://picsum.photos/200/300"
            ),
            QuoteEntity(
                size = WidgetSize.LARGE,
                imageUrl = "https://picsum.photos/200/300"
            ),

            // Set 2 - Inspirational
            QuoteEntity(
                size = WidgetSize.SMALL,
                imageUrl = "https://picsum.photos/200/300"
            ),
            QuoteEntity(
                size = WidgetSize.MEDIUM,
                imageUrl = "https://picsum.photos/200/300"
            ),
            QuoteEntity(
                size = WidgetSize.LARGE,
                imageUrl = "https://picsum.photos/200/300"
            ),
        )

        mainViewModel.insertQuotes(sampleQuotes)
    }


}

private enum class ScreenState {
    TYPE_SELECTION,
    QUOTE_SELECTION
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