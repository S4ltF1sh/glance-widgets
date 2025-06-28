package com.s4ltf1sh.glance_widgets.widget.widget.calendar

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.LocalContext
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
import com.s4ltf1sh.glance_widgets.model.GlanceWidget
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetSize
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetType
import com.s4ltf1sh.glance_widgets.widget.core.BaseAppWidget
import com.s4ltf1sh.glance_widgets.widget.widget.calendar.component.CalendarType1
import com.s4ltf1sh.glance_widgets.widget.widget.calendar.component.CalendarType2
import com.s4ltf1sh.glance_widgets.widget.widget.calendar.component.CalendarType3
import com.s4ltf1sh.glance_widgets.widget.widget.calendar.component.CalendarType5
import com.s4ltf1sh.glance_widgets.widget.widget.quotes.getImageProvider
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.util.Calendar
import java.util.Locale

private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

@Composable
fun CalendarWidget(
    glanceWidget: GlanceWidget,
    widgetId: Int
) {
    val context = LocalContext.current
    val milliSecond = remember {
        mutableLongStateOf(System.currentTimeMillis())
    }

    val calendar = Calendar.getInstance()
        .setSundayAsFirstDayOfWeek()
        .setTimeInMilliSecond(milliSecond.longValue)

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .cornerRadius(16.dp)
            .clickable(
                actionStartActivity<MainActivity>(
                    parameters = actionParametersOf(
                        BaseAppWidget.KEY_WIDGET_ID to widgetId,
                        BaseAppWidget.KEY_WIDGET_TYPE to glanceWidget.type.typeId,
                        BaseAppWidget.KEY_WIDGET_SIZE to glanceWidget.size.name
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        if (glanceWidget.data.isEmpty()) {
            CalendarEmptyState()
            return@Box
        }

        val calendarData = try {
            moshi.adapter(WidgetCalendarData::class.java).fromJson(glanceWidget.data)
        } catch (e: Exception) {
            Log.d("CalendarWidget", "Error parsing calendar data: ${e.message}")
            null
        }

        if (calendarData != null) {
            CalendarContent(
                context = context,
                calendar = calendar,
                calendarData = calendarData,
                glanceWidgetType = glanceWidget.type as GlanceWidgetType.Calendar,
                glanceWidgetSize = glanceWidget.size,
                gotoNextMonth = {
                    val nextMonth = calendar.apply {
                        add(Calendar.MONTH, 1) // Move to the next month
                    }
                    milliSecond.longValue = nextMonth.timeInMillis
                },
                gotoPreviousMonth = {
                    val previousMonth = calendar.apply {
                        add(Calendar.MONTH, -1) // Move to the previous month
                    }
                    milliSecond.longValue = previousMonth.timeInMillis
                }
            )
        } else {
            CalendarErrorState()
        }
    }
}

@SuppressLint("RestrictedApi")
@Composable
private fun CalendarContent(
    context: Context,
    calendar: Calendar,
    calendarData: WidgetCalendarData,
    glanceWidgetType: GlanceWidgetType.Calendar,
    glanceWidgetSize: GlanceWidgetSize,
    gotoNextMonth: () -> Unit = {},
    gotoPreviousMonth: () -> Unit = {}
) {
    Box(
        modifier = GlanceModifier.fillMaxSize(),
        contentAlignment = Alignment.Center
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

        when (glanceWidgetType) {
            GlanceWidgetType.Calendar.Type1Glance -> CalendarType1(
                glanceWidgetSize = glanceWidgetSize,
                calendar = calendar,
                onGoToPreviousMonth = gotoPreviousMonth,
                onGoToNextMonth = gotoNextMonth,
            )

            GlanceWidgetType.Calendar.Type2Glance, GlanceWidgetType.Calendar.Type4Glance -> CalendarType2(
                glanceWidgetSize = glanceWidgetSize,
                calendar = calendar,
                dayOfWeekNames = getDayOfWeekNamesFromResources(context, glanceWidgetType, glanceWidgetSize),
                onGoToPreviousMonth = gotoPreviousMonth,
                onGoToNextMonth = gotoNextMonth
            )

            GlanceWidgetType.Calendar.Type3Glance -> CalendarType3(
                glanceWidgetSize = glanceWidgetSize,
                calendar = calendar,
                dayOfWeekNames = getDayOfWeekNamesFromResources(context, glanceWidgetType, glanceWidgetSize),
            )

            GlanceWidgetType.Calendar.Type5Glance -> CalendarType5(
                glanceWidgetSize = glanceWidgetSize,
                calendar = calendar,
                dayOfWeekNames = getDayOfWeekNamesFromResources(context, glanceWidgetType, glanceWidgetSize)
            )
        }
    }
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
    glanceWidgetType: GlanceWidgetType.Calendar,
    glanceWidgetSize: GlanceWidgetSize,
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
        if (glanceWidgetType == GlanceWidgetType.Calendar.Type2Glance || (glanceWidgetType == GlanceWidgetType.Calendar.Type5Glance && glanceWidgetSize == GlanceWidgetSize.LARGE)) {
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

private fun Calendar.setTimeInMilliSecond(millis: Long): Calendar {
    this.timeInMillis = millis
    return this
}

private fun Calendar.setSundayAsFirstDayOfWeek(): Calendar {
    this.firstDayOfWeek = Calendar.SUNDAY
    return this
}