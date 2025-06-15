package com.s4ltf1sh.glance_widgets

import android.appwidget.AppWidgetManager
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.s4ltf1sh.glance_widgets.db.quote.QuoteEntity
import com.s4ltf1sh.glance_widgets.model.WidgetSize
import com.s4ltf1sh.glance_widgets.model.WidgetType
import com.s4ltf1sh.glance_widgets.ui.screen.ConfigurationScreen
import com.s4ltf1sh.glance_widgets.ui.screen.PhotoSelectionScreen
import com.s4ltf1sh.glance_widgets.ui.screen.QuoteSelectionScreen
import com.s4ltf1sh.glance_widgets.ui.theme.GlancewidgetsTheme
import com.s4ltf1sh.glance_widgets.widget.core.BaseAppWidget
import com.s4ltf1sh.glance_widgets.widget.widget.quotes.QuotesWidgetWorker
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

        Log.d("MainActivity", "Widget ID: $widgetId, Type: $currentType, Size: $currentSize")

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
                                    if (type == WidgetType.QUOTE)
                                        screenState = ScreenState.QUOTE_SELECTION
                                    else if (type == WidgetType.PHOTO) {
                                        screenState = ScreenState.PHOTO_SELECTION
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
                                QuotesWidgetWorker.enqueue(
                                    context = this@MainActivity,
                                    widgetId = widgetId,
                                    type = currentType,
                                    widgetSize = currentSize,
                                    imageUrl = quote.imageUrl
                                )

                                finish()
                            }
                        )
                    }

                    ScreenState.PHOTO_SELECTION -> {
                        PhotoSelectionScreen(
                            widgetId = widgetId,
                            onBackPressed = {
                                screenState = ScreenState.TYPE_SELECTION
                            },
                            onCloseActivity = {
                                finish()
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
    QUOTE_SELECTION,
    PHOTO_SELECTION
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