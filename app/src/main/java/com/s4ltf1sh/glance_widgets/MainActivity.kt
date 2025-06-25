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
import com.s4ltf1sh.glance_widgets.db.calendar.CalendarEntity
import com.s4ltf1sh.glance_widgets.db.clock.ClockAnalogEntity
import com.s4ltf1sh.glance_widgets.db.clock.ClockDigitalEntity
import com.s4ltf1sh.glance_widgets.db.quote.QuoteEntity
import com.s4ltf1sh.glance_widgets.model.WidgetSize
import com.s4ltf1sh.glance_widgets.model.WidgetType
import com.s4ltf1sh.glance_widgets.ui.screen.CalendarSelectionScreen
import com.s4ltf1sh.glance_widgets.ui.screen.ClockAnalogSelectionScreen
import com.s4ltf1sh.glance_widgets.ui.screen.ClockDigitalSelectionScreen
import com.s4ltf1sh.glance_widgets.ui.screen.ConfigurationScreen
import com.s4ltf1sh.glance_widgets.ui.screen.PhotoSelectionScreen
import com.s4ltf1sh.glance_widgets.ui.screen.QuoteSelectionScreen
import com.s4ltf1sh.glance_widgets.ui.theme.GlancewidgetsTheme
import com.s4ltf1sh.glance_widgets.widget.core.BaseAppWidget
import com.s4ltf1sh.glance_widgets.widget.widget.calendar.CalendarWidgetWorker
import com.s4ltf1sh.glance_widgets.widget.widget.clock.analog.ClockAnalogWidgetWorker
import com.s4ltf1sh.glance_widgets.widget.widget.clock.digital.ClockDigitalWidgetWorker
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
            WidgetType.fromTypeId(it)
        } ?: WidgetType.None

        val currentSize = intent.getStringExtra(BaseAppWidget.KEY_WIDGET_SIZE.name)?.let {
            WidgetSize.valueOf(it)
        } ?: WidgetSize.SMALL

        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        Log.d("MainActivity", "Widget ID: $widgetId, Type: $currentType, Size: $currentSize")

        // Load data based on current type
        when (currentType) {
            is WidgetType.Quote -> mainViewModel.getQuotesBySize(currentSize)
            is WidgetType.Clock.Digital -> mainViewModel.getClockDigitalsBySize(currentSize)
            is WidgetType.Clock.Analog -> mainViewModel.getClockAnalogsBySize(currentSize)
            else -> {
                // Load quotes by default for new widgets
                mainViewModel.getQuotesBySize(currentSize)
            }
        }

        setContent {
            val quotes = mainViewModel.quotes.collectAsStateWithLifecycle()
            val clockDigitals = mainViewModel.clockDigitals.collectAsStateWithLifecycle()
            val clockAnalogs = mainViewModel.clockAnalogs.collectAsStateWithLifecycle()

            GlancewidgetsTheme {
                var screenState by remember { mutableStateOf(getInitialScreenState(currentType)) }
                var selectedClockType by remember { mutableStateOf<WidgetType.Clock.Digital?>(null) }

                when (screenState) {
                    ScreenState.TYPE_SELECTION -> {
                        if (currentType == WidgetType.None) {
                            ConfigurationScreen(
                                widgetId = widgetId,
                                currentType = currentType,
                                onTypeSelected = { type ->
                                    when (type) {
                                        WidgetType.Quote -> {
                                            screenState = ScreenState.QUOTE_SELECTION
                                        }

                                        WidgetType.Photo -> {
                                            screenState = ScreenState.PHOTO_SELECTION
                                        }

                                        is WidgetType.Clock.Digital -> {
                                            selectedClockType = type
                                            mainViewModel.getClockDigitalsBySize(currentSize)
                                            screenState = ScreenState.CLOCK_DIGITAL_SELECTION
                                        }

                                        is WidgetType.Clock.Analog -> {
                                            // For analog clocks, you might want to show a selection screen
                                            mainViewModel.getClockAnalogsBySize(currentSize)
                                            screenState = ScreenState.CLOCK_ANALOG_SELECTION
                                        }

                                        is WidgetType.Calendar -> {
                                            screenState = ScreenState.CALENDAR_SELECTION
                                        }

                                        else -> {
                                            Log.w("MainActivity", "Unsupported widget type: $type")
                                        }
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

                    ScreenState.CLOCK_DIGITAL_SELECTION -> {
                        val clockType =
                            selectedClockType ?: currentType as? WidgetType.Clock.Digital

                        if (clockType != null) {
                            ClockDigitalSelectionScreen(
                                widgetSize = currentSize,
                                clockType = clockType,
                                clockDigitalBackgrounds = clockDigitals.value,
                                onClockDigitalSelected = { clockDigital ->
                                    ClockDigitalWidgetWorker.enqueue(
                                        context = this@MainActivity,
                                        widgetId = widgetId,
                                        type = clockType,
                                        widgetSize = currentSize,
                                        backgroundUrl = clockDigital.backgroundUrl
                                    )

                                    finish()
                                }
                            )
                        } else {
                            // Fallback to type selection if no clock type
                            screenState = ScreenState.TYPE_SELECTION
                        }
                    }

                    ScreenState.CLOCK_ANALOG_SELECTION -> {
                        ClockAnalogSelectionScreen(
                            widgetSize = currentSize,
                            clockAnalogBackgrounds = clockAnalogs.value,
                            onClockAnalogSelected = { clockAnalog ->
                                ClockAnalogWidgetWorker.enqueue(
                                    context = this@MainActivity,
                                    widgetId = widgetId,
                                    type = clockAnalog.type as WidgetType.Clock.Analog,
                                    widgetSize = currentSize,
                                    backgroundUrl = clockAnalog.backgroundUrl
                                )

                                finish()
                            }
                        )
                    }

                    ScreenState.CALENDAR_SELECTION -> {
                        val calendarType =
                            currentType as? WidgetType.Calendar ?: WidgetType.Calendar.Type1

                        CalendarSelectionScreen(
                            widgetId = widgetId,
                            widgetSize = currentSize,
                            calendarType = calendarType,
                            onBackPressed = {
                                screenState = ScreenState.TYPE_SELECTION
                            },
                            onCalendarSelected = { calendar ->
                                CalendarWidgetWorker.enqueue(
                                    context = this,
                                    widgetId = widgetId,
                                    type = calendar.type,
                                    widgetSize = calendar.size,
                                    backgroundImageUrl = calendar.backgroundUrl
                                )
                                finish()
                            }
                        )
                    }
                }
            }
        }

        // Initialize sample data if needed
        lifecycleScope.launch {
            initializeSampleQuotes()
            initializeSampleClockDigital()
            initializeSampleClockAnalogs()
            initializeSampleCalendars()
        }
    }

    private fun getInitialScreenState(currentType: WidgetType): ScreenState {
        return when (currentType) {
            WidgetType.None -> ScreenState.TYPE_SELECTION
            WidgetType.Quote -> ScreenState.QUOTE_SELECTION
            WidgetType.Photo -> ScreenState.PHOTO_SELECTION
            is WidgetType.Clock.Digital -> ScreenState.CLOCK_DIGITAL_SELECTION
            is WidgetType.Clock.Analog -> ScreenState.CLOCK_ANALOG_SELECTION
            is WidgetType.Calendar -> ScreenState.CALENDAR_SELECTION
            else -> ScreenState.TYPE_SELECTION
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

    private fun initializeSampleClockDigital() {
        // Add sample clock digital backgrounds - replace with your actual clock background images
        val sampleClockDigitals = listOf(
            // Type1 backgrounds
            ClockDigitalEntity(
                size = WidgetSize.SMALL,
                type = WidgetType.Clock.Digital.Type1,
                backgroundUrl = "https://picsum.photos/400/400?random=1"
            ),
            ClockDigitalEntity(
                size = WidgetSize.MEDIUM,
                type = WidgetType.Clock.Digital.Type1,
                backgroundUrl = "https://picsum.photos/800/400?random=2"
            ),
            ClockDigitalEntity(
                size = WidgetSize.LARGE,
                type = WidgetType.Clock.Digital.Type1,
                backgroundUrl = "https://picsum.photos/400/400?random=3"
            ),

            // Type2 backgrounds
            ClockDigitalEntity(
                size = WidgetSize.SMALL,
                type = WidgetType.Clock.Digital.Type2,
                backgroundUrl = "https://picsum.photos/400/400?random=4"
            ),
            ClockDigitalEntity(
                size = WidgetSize.MEDIUM,
                type = WidgetType.Clock.Digital.Type2,
                backgroundUrl = "https://picsum.photos/800/400?random=5"
            ),
            ClockDigitalEntity(
                size = WidgetSize.LARGE,
                type = WidgetType.Clock.Digital.Type2,
                backgroundUrl = "https://picsum.photos/400/400?random=6"
            ),

            // Additional backgrounds for variety
            ClockDigitalEntity(
                size = WidgetSize.SMALL,
                type = WidgetType.Clock.Digital.Type1,
                backgroundUrl = "https://picsum.photos/400/400?random=7"
            ),
            ClockDigitalEntity(
                size = WidgetSize.MEDIUM,
                type = WidgetType.Clock.Digital.Type2,
                backgroundUrl = "https://picsum.photos/800/400?random=8"
            ),
        )

        mainViewModel.insertClockDigitals(sampleClockDigitals)
    }

    private fun initializeSampleClockAnalogs() {
        // Add sample clock analogs - replace with your actual clock images
        val sampleClockAnalogs = listOf(
            // Analog clock backgrounds
            ClockAnalogEntity(
                size = WidgetSize.SMALL,
                type = WidgetType.Clock.Analog.Type1,
                backgroundUrl = "https://picsum.photos/400/400",
            ),
            ClockAnalogEntity(
                size = WidgetSize.SMALL,
                type = WidgetType.Clock.Analog.Type1,
                backgroundUrl = "https://picsum.photos/400/400"
            ),
            ClockAnalogEntity(
                size = WidgetSize.MEDIUM,
                type = WidgetType.Clock.Analog.Type2,
                backgroundUrl = "https://picsum.photos/800/400",
            ),
            ClockAnalogEntity(
                size = WidgetSize.LARGE,
                type = WidgetType.Clock.Analog.Type2,
                backgroundUrl = "https://picsum.photos/400/400"
            )
        )

        mainViewModel.insertClockAnalogs(sampleClockAnalogs)
    }

    private fun initializeSampleCalendars() {
        // Add sample calendars - replace with your actual calendar images
        val sampleCalendars = listOf(
            // Calendar Type1
            CalendarEntity(
                size = WidgetSize.SMALL,
                type = WidgetType.Calendar.Type1,
                backgroundUrl = "https://picsum.photos/400/400?random=1"
            ),
            CalendarEntity(
                size = WidgetSize.MEDIUM,
                type = WidgetType.Calendar.Type1,
                backgroundUrl = "https://picsum.photos/800/400?random=2"
            ),
            CalendarEntity(
                size = WidgetSize.LARGE,
                type = WidgetType.Calendar.Type1,
                backgroundUrl = "https://picsum.photos/400/400?random=3"
            ),

            // Calendar Type2
            CalendarEntity(
                size = WidgetSize.SMALL,
                type = WidgetType.Calendar.Type2,
                backgroundUrl = "https://picsum.photos/400/400?random=4"
            ),
            CalendarEntity(
                size = WidgetSize.MEDIUM,
                type = WidgetType.Calendar.Type2,
                backgroundUrl = "https://picsum.photos/800/400?random=5"
            ),
            CalendarEntity(
                size = WidgetSize.LARGE,
                type = WidgetType.Calendar.Type2,
                backgroundUrl = "https://picsum.photos/400/400?random=6"
            )
        )

        mainViewModel.insertCalendars(sampleCalendars)
    }
}

private enum class ScreenState {
    TYPE_SELECTION,
    QUOTE_SELECTION,
    PHOTO_SELECTION,
    CLOCK_DIGITAL_SELECTION,
    CLOCK_ANALOG_SELECTION,
    CALENDAR_SELECTION
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