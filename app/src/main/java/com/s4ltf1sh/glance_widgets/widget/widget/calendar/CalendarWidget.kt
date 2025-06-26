package com.s4ltf1sh.glance_widgets.widget.widget.calendar

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.action
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.ContentScale
import androidx.glance.layout.fillMaxSize
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.s4ltf1sh.glance_widgets.MainActivity
import com.s4ltf1sh.glance_widgets.R
import com.s4ltf1sh.glance_widgets.db.calendar.WidgetCalendarData
import com.s4ltf1sh.glance_widgets.model.Widget
import com.s4ltf1sh.glance_widgets.model.WidgetSize
import com.s4ltf1sh.glance_widgets.model.WidgetType
import com.s4ltf1sh.glance_widgets.ui.theme.MonthCalendarColors
import com.s4ltf1sh.glance_widgets.utils.CalendarWidgetUtils
import com.s4ltf1sh.glance_widgets.utils.toColor
import com.s4ltf1sh.glance_widgets.widget.widget.calendar.component.CalendarViewDefault
import com.s4ltf1sh.glance_widgets.widget.core.BaseAppWidget
import com.s4ltf1sh.glance_widgets.widget.widget.quotes.getImageProvider
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.util.Calendar
import java.util.Locale

private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

@Composable
fun CalendarWidget(
    widget: Widget,
    widgetId: Int
) {
    val context = LocalContext.current
    val milliSecond = remember {
        mutableLongStateOf(System.currentTimeMillis())
    }

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .cornerRadius(16.dp)
            .clickable(
                actionStartActivity<MainActivity>(
                    parameters = actionParametersOf(
                        BaseAppWidget.KEY_WIDGET_ID to widgetId,
                        BaseAppWidget.KEY_WIDGET_TYPE to widget.type.typeId,
                        BaseAppWidget.KEY_WIDGET_SIZE to widget.size.name
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        if (widget.data.isNotEmpty()) {
            val calendarData = try {
                moshi.adapter(WidgetCalendarData::class.java).fromJson(widget.data)
            } catch (e: Exception) {
                null
            }

            if (calendarData != null) {
                CalendarContent(
                    context = context,
                    calendarData = calendarData.copy(currentMonth = milliSecond.longValue),
                    widgetType = widget.type as WidgetType.Calendar,
                    widgetSize = widget.size,
                    gotoNextMonth = {
                        val calendar = Calendar.getInstance().setSundayAsFirstDayOfWeek().apply {
                            timeInMillis = milliSecond.longValue
                            add(Calendar.MONTH, 1) // Move to the next month
                        }
                        milliSecond.longValue = calendar.timeInMillis
                    },
                    gotoPreviousMonth = {
                        val calendar = Calendar.getInstance().setSundayAsFirstDayOfWeek().apply {
                            timeInMillis = milliSecond.longValue
                            add(Calendar.MONTH, -1) // Move to the previous month
                        }
                        milliSecond.longValue = calendar.timeInMillis
                    }
                )
            } else {
                CalendarErrorState()
            }
        } else {
            CalendarEmptyState()
        }
    }
}

@SuppressLint("RestrictedApi")
@Composable
private fun CalendarContent(
    context: Context,
    calendarData: WidgetCalendarData,
    widgetType: WidgetType.Calendar,
    widgetSize: WidgetSize,
    gotoNextMonth: () -> Unit = {},
    gotoPreviousMonth: () -> Unit = {}
) {
    Box(
        modifier = GlanceModifier.fillMaxSize()
    ) {
        // Background image if available
        calendarData.backgroundPath?.let { imagePath ->
            Image(
                provider = getImageProvider(imagePath),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = GlanceModifier.fillMaxSize()
            )
        }

        CalendarViewDefault(
            modifier = GlanceModifier.fillMaxSize(),
            calendar = CalendarWidgetUtils.getCalendar(calendarData.currentMonth)
                .setSundayAsFirstDayOfWeek(),
            onGoToPreviousMonth = gotoPreviousMonth,
            onGoToNextMonth = gotoNextMonth,
            onDateClick = action { },
            dayOfWeekNames = getDayOfWeekNamesFromResources(context, widgetType, widgetSize),
            monthCalendarColors = MonthCalendarColors(
                background = ColorProvider(Color.Transparent),
                weekDayTextColor = ColorProvider("#FF9330".toColor()),
                iconColor = ColorProvider(Color.White),
                dateTextColor = ColorProvider(Color.White),
                todayDateTextColor = ColorProvider(Color.White),
                unfocusedDateTextColor = ColorProvider(Color(0x91FFFFFF)),
                monthYearHeaderColor = ColorProvider(Color.White),
            ),
            selectedDateBackground = {
                selectedDateBackground(widgetType)
            }
        )
    }
}

private fun selectedDateBackground(widgetType: WidgetType.Calendar): ImageProvider {
    return ImageProvider(
        when (widgetType) {
            is WidgetType.Calendar.Type2 -> R.drawable.calendar_selected_bg_2
            is WidgetType.Calendar.Type3 -> R.drawable.calendar_selected_bg_3
            is WidgetType.Calendar.Type5 -> R.drawable.calendar_selected_bg_5
            else -> R.drawable.calendar_selected_bg_3 // Default background
        }
    )
}

@SuppressLint("RestrictedApi")
@Composable
private fun CalendarEmptyState() {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Tap to setup calendar",
            style = TextStyle(
                color = ColorProvider(Color(0xFF666666)),
                fontSize = 14.sp
            )
        )
    }
}

@SuppressLint("RestrictedApi")
@Composable
private fun CalendarErrorState() {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Unable to load calendar",
            style = TextStyle(
                color = ColorProvider(Color(0xFF666666)),
                fontSize = 14.sp
            )
        )
    }
}

fun getDayOfWeekNamesFromResources(
    context: Context,
    widgetType: WidgetType.Calendar,
    widgetSize: WidgetSize,
    forceLocale: Locale? = null
): List<String> {
    val targetContext = if (forceLocale != null) {
        val config = context.resources.configuration
        config.setLocale(forceLocale)
        context.createConfigurationContext(config)
    } else {
        context
    }

    val arrayResId =
        if (widgetType == WidgetType.Calendar.Type2 || (widgetType == WidgetType.Calendar.Type5 && widgetSize == WidgetSize.LARGE)) {
            R.array.day_names_short
        } else {
            R.array.day_names_minimal
        }

    val dayNames = targetContext.resources.getStringArray(arrayResId).toList()

    return if (dayNames.size >= 7) {
        dayNames.take(7)
    } else {
        listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    }
}

fun Calendar.setSundayAsFirstDayOfWeek(): Calendar {
    this.firstDayOfWeek = Calendar.SUNDAY
    return this
}