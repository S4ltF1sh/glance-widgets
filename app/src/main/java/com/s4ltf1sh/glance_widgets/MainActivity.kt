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
import com.s4ltf1sh.glance_widgets.db.calendar.GlanceCalendarEntity
import com.s4ltf1sh.glance_widgets.db.clock.GlanceClockAnalogEntity
import com.s4ltf1sh.glance_widgets.db.clock.GlanceClockDigitalEntity
import com.s4ltf1sh.glance_widgets.db.quote.GlanceQuoteEntity
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetSize
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetType
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
            GlanceWidgetType.fromTypeId(it)
        } ?: GlanceWidgetType.None

        val currentSize = intent.getStringExtra(BaseAppWidget.KEY_WIDGET_SIZE.name)?.let {
            GlanceWidgetSize.valueOf(it)
        } ?: GlanceWidgetSize.SMALL

        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        Log.d("MainActivity", "Widget ID: $widgetId, Type: $currentType, Size: $currentSize")

        // Load data based on current type
        when (currentType) {
            is GlanceWidgetType.Quote -> mainViewModel.getQuotesBySize(currentSize)
            is GlanceWidgetType.Clock.Digital -> mainViewModel.getClockDigitalsBySize(currentSize)
            is GlanceWidgetType.Clock.Analog -> mainViewModel.getClockAnalogsBySize(currentSize)
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
                var selectedClockType by remember { mutableStateOf<GlanceWidgetType.Clock.Digital?>(null) }

                when (screenState) {
                    ScreenState.TYPE_SELECTION -> {
                        if (currentType == GlanceWidgetType.None) {
                            ConfigurationScreen(
                                widgetId = widgetId,
                                currentType = currentType,
                                onTypeSelected = { type ->
                                    when (type) {
                                        GlanceWidgetType.Quote -> {
                                            screenState = ScreenState.QUOTE_SELECTION
                                        }

                                        GlanceWidgetType.Photo -> {
                                            screenState = ScreenState.PHOTO_SELECTION
                                        }

                                        is GlanceWidgetType.Clock.Digital -> {
                                            selectedClockType = type
                                            mainViewModel.getClockDigitalsBySize(currentSize)
                                            screenState = ScreenState.CLOCK_DIGITAL_SELECTION
                                        }

                                        is GlanceWidgetType.Clock.Analog -> {
                                            // For analog clocks, you might want to show a selection screen
                                            mainViewModel.getClockAnalogsBySize(currentSize)
                                            screenState = ScreenState.CLOCK_ANALOG_SELECTION
                                        }

                                        is GlanceWidgetType.Calendar -> {
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
                            glanceWidgetSize = currentSize,
                            quotes = quotes.value,
                            onQuoteSelected = { quote ->
                                QuotesWidgetWorker.enqueue(
                                    context = this@MainActivity,
                                    widgetId = widgetId,
                                    type = currentType,
                                    glanceWidgetSize = currentSize,
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
                            selectedClockType ?: currentType as? GlanceWidgetType.Clock.Digital

                        if (clockType != null) {
                            ClockDigitalSelectionScreen(
                                glanceWidgetSize = currentSize,
                                clockType = clockType,
                                clockDigitalBackgrounds = clockDigitals.value,
                                onClockDigitalSelected = { clockDigital ->
                                    ClockDigitalWidgetWorker.enqueue(
                                        context = this@MainActivity,
                                        widgetId = widgetId,
                                        type = clockType,
                                        glanceWidgetSize = currentSize,
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
                            glanceWidgetSize = currentSize,
                            clockAnalogBackgrounds = clockAnalogs.value,
                            onClockAnalogSelected = { clockAnalog ->
                                ClockAnalogWidgetWorker.enqueue(
                                    context = this@MainActivity,
                                    widgetId = widgetId,
                                    type = clockAnalog.type as GlanceWidgetType.Clock.Analog,
                                    glanceWidgetSize = currentSize,
                                    backgroundUrl = clockAnalog.backgroundUrl
                                )

                                finish()
                            }
                        )
                    }

                    ScreenState.CALENDAR_SELECTION -> {
                        val calendarType =
                            currentType as? GlanceWidgetType.Calendar ?: GlanceWidgetType.Calendar.Type1Glance

                        CalendarSelectionScreen(
                            widgetId = widgetId,
                            glanceWidgetSize = currentSize,
                            calendarType = calendarType,
                            onBackPressed = {
                                screenState = ScreenState.TYPE_SELECTION
                            },
                            onCalendarSelected = { calendar ->
                                CalendarWidgetWorker.enqueue(
                                    context = this,
                                    widgetId = widgetId,
                                    type = calendar.type,
                                    glanceWidgetSize = calendar.size,
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

    private fun getInitialScreenState(currentType: GlanceWidgetType): ScreenState {
        return when (currentType) {
            GlanceWidgetType.None -> ScreenState.TYPE_SELECTION
            GlanceWidgetType.Quote -> ScreenState.QUOTE_SELECTION
            GlanceWidgetType.Photo -> ScreenState.PHOTO_SELECTION
            is GlanceWidgetType.Clock.Digital -> ScreenState.CLOCK_DIGITAL_SELECTION
            is GlanceWidgetType.Clock.Analog -> ScreenState.CLOCK_ANALOG_SELECTION
            is GlanceWidgetType.Calendar -> ScreenState.CALENDAR_SELECTION
            else -> ScreenState.TYPE_SELECTION
        }
    }

    private fun initializeSampleQuotes() {
        // Add sample quotes - replace with your actual quote images
        val sampleQuotes = listOf(
            // Set 1 - Motivational
            GlanceQuoteEntity(
                size = GlanceWidgetSize.SMALL,
                imageUrl = "https://picsum.photos/400"
            ),
            GlanceQuoteEntity(
                size = GlanceWidgetSize.MEDIUM,
                imageUrl = "https://picsum.photos/200/300"
            ),
            GlanceQuoteEntity(
                size = GlanceWidgetSize.LARGE,
                imageUrl = "https://picsum.photos/200/300"
            ),

            // Set 2 - Inspirational
            GlanceQuoteEntity(
                size = GlanceWidgetSize.SMALL,
                imageUrl = "https://picsum.photos/200/300"
            ),
            GlanceQuoteEntity(
                size = GlanceWidgetSize.MEDIUM,
                imageUrl = "https://picsum.photos/200/300"
            ),
            GlanceQuoteEntity(
                size = GlanceWidgetSize.LARGE,
                imageUrl = "https://picsum.photos/200/300"
            ),
        )

        mainViewModel.insertQuotes(sampleQuotes)
    }

    private fun initializeSampleClockDigital() {
        // Add sample clock digital backgrounds - replace with your actual clock background images
        val sampleClockDigitals = listOf(
            // Type1 backgrounds
            GlanceClockDigitalEntity(
                size = GlanceWidgetSize.SMALL,
                type = GlanceWidgetType.Clock.Digital.Type1Glance,
                backgroundUrl = "https://picsum.photos/400/400?random=1"
            ),
            GlanceClockDigitalEntity(
                size = GlanceWidgetSize.MEDIUM,
                type = GlanceWidgetType.Clock.Digital.Type1Glance,
                backgroundUrl = "https://picsum.photos/800/400?random=2"
            ),
            GlanceClockDigitalEntity(
                size = GlanceWidgetSize.LARGE,
                type = GlanceWidgetType.Clock.Digital.Type1Glance,
                backgroundUrl = "https://picsum.photos/400/400?random=3"
            ),

            // Type2 backgrounds
            GlanceClockDigitalEntity(
                size = GlanceWidgetSize.SMALL,
                type = GlanceWidgetType.Clock.Digital.Type2Glance,
                backgroundUrl = "https://picsum.photos/400/400?random=4"
            ),
            GlanceClockDigitalEntity(
                size = GlanceWidgetSize.MEDIUM,
                type = GlanceWidgetType.Clock.Digital.Type2Glance,
                backgroundUrl = "https://picsum.photos/800/400?random=5"
            ),
            GlanceClockDigitalEntity(
                size = GlanceWidgetSize.LARGE,
                type = GlanceWidgetType.Clock.Digital.Type2Glance,
                backgroundUrl = "https://picsum.photos/400/400?random=6"
            ),

            // Additional backgrounds for variety
            GlanceClockDigitalEntity(
                size = GlanceWidgetSize.SMALL,
                type = GlanceWidgetType.Clock.Digital.Type1Glance,
                backgroundUrl = "https://picsum.photos/400/400?random=7"
            ),
            GlanceClockDigitalEntity(
                size = GlanceWidgetSize.MEDIUM,
                type = GlanceWidgetType.Clock.Digital.Type2Glance,
                backgroundUrl = "https://picsum.photos/800/400?random=8"
            ),
        )

        mainViewModel.insertClockDigitals(sampleClockDigitals)
    }

    private fun initializeSampleClockAnalogs() {
        // Add sample clock analogs - replace with your actual clock images
        val sampleClockAnalogs = listOf(
            // Analog clock backgrounds
            GlanceClockAnalogEntity(
                size = GlanceWidgetSize.SMALL,
                type = GlanceWidgetType.Clock.Analog.Type1Glance,
                backgroundUrl = "https://picsum.photos/400/400",
            ),
            GlanceClockAnalogEntity(
                size = GlanceWidgetSize.SMALL,
                type = GlanceWidgetType.Clock.Analog.Type1Glance,
                backgroundUrl = "https://picsum.photos/400/400"
            ),
            GlanceClockAnalogEntity(
                size = GlanceWidgetSize.MEDIUM,
                type = GlanceWidgetType.Clock.Analog.Type2Glance,
                backgroundUrl = "https://picsum.photos/800/400",
            ),
            GlanceClockAnalogEntity(
                size = GlanceWidgetSize.LARGE,
                type = GlanceWidgetType.Clock.Analog.Type2Glance,
                backgroundUrl = "https://picsum.photos/400/400"
            )
        )

        mainViewModel.insertClockAnalogs(sampleClockAnalogs)
    }

    private fun initializeSampleCalendars() {
        // Add sample calendars - replace with your actual calendar images
        val sampleCalendars = listOf(
            // Calendar Type1
            GlanceCalendarEntity(
                size = GlanceWidgetSize.SMALL,
                type = GlanceWidgetType.Calendar.Type1Glance,
                backgroundUrl = "https://picsum.photos/400/400?random=1"
            ),
            GlanceCalendarEntity(
                size = GlanceWidgetSize.MEDIUM,
                type = GlanceWidgetType.Calendar.Type1Glance,
                backgroundUrl = "https://picsum.photos/800/400?random=2"
            ),
            GlanceCalendarEntity(
                size = GlanceWidgetSize.LARGE,
                type = GlanceWidgetType.Calendar.Type1Glance,
                backgroundUrl = "https://picsum.photos/400/400?random=3"
            ),

            // Calendar Type2
            GlanceCalendarEntity(
                size = GlanceWidgetSize.SMALL,
                type = GlanceWidgetType.Calendar.Type2Glance,
                backgroundUrl = "https://picsum.photos/400/400?random=4"
            ),
            GlanceCalendarEntity(
                size = GlanceWidgetSize.MEDIUM,
                type = GlanceWidgetType.Calendar.Type2Glance,
                backgroundUrl = "https://picsum.photos/800/400?random=5"
            ),
            GlanceCalendarEntity(
                size = GlanceWidgetSize.LARGE,
                type = GlanceWidgetType.Calendar.Type2Glance,
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