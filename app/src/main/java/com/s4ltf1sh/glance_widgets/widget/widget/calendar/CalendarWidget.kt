package com.s4ltf1sh.glance_widgets.widget.widget.calendar

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.s4ltf1sh.glance_widgets.MainActivity
import com.s4ltf1sh.glance_widgets.db.calendar.WidgetCalendarData
import com.s4ltf1sh.glance_widgets.model.Widget
import com.s4ltf1sh.glance_widgets.model.WidgetSize
import com.s4ltf1sh.glance_widgets.utils.CalendarUtils
import com.s4ltf1sh.glance_widgets.widget.core.BaseAppWidget
import com.s4ltf1sh.glance_widgets.widget.widget.quotes.getImageProvider
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.util.Calendar

@Composable
fun CalendarWidget(
    widget: Widget,
    widgetId: Int
) {
    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

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
                    calendarData = calendarData,
                    widgetSize = widget.size
                )
            } else {
                CalendarErrorState()
            }
        } else {
            CalendarEmptyState()
        }
    }
}

@Composable
private fun CalendarContent(
    calendarData: WidgetCalendarData,
    widgetSize: WidgetSize
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

        // Calendar overlay
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(Color(0x40000000))
                .padding(8.dp)
        ) {
            when (widgetSize) {
                WidgetSize.SMALL -> SmallCalendarLayout(calendarData)
                WidgetSize.MEDIUM -> MediumCalendarLayout(calendarData)
                WidgetSize.LARGE -> LargeCalendarLayout(calendarData)
            }
        }
    }
}

@Composable
private fun SmallCalendarLayout(calendarData: WidgetCalendarData) {
    val today = Calendar.getInstance()
    val monthName = CalendarUtils.getMonthName(calendarData.month, isShort = true)

    Column(
        modifier = GlanceModifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Month and year
        Text(
            text = "$monthName ${calendarData.year}",
            style = getTextStyle(isHeader = true, size = 14.sp),
            modifier = GlanceModifier.fillMaxWidth()
        )

        Spacer(modifier = GlanceModifier.height(4.dp))

        // Today's date (large)
        val displayDay = calendarData.todayDay ?: today.get(Calendar.DAY_OF_MONTH)
        Text(
            text = displayDay.toString(),
            style = getTextStyle(size = 24.sp),
            modifier = GlanceModifier.fillMaxWidth()
        )

        // Day of week
        val dayOfWeek = if (calendarData.todayDay != null) {
            CalendarUtils.getDayOfWeekName(
                calendarData.year,
                calendarData.month,
                calendarData.todayDay,
                isShort = true
            )
        } else {
            CalendarUtils.getDayOfWeekName(
                today.get(Calendar.YEAR),
                today.get(Calendar.MONTH) + 1,
                today.get(Calendar.DAY_OF_MONTH),
                isShort = true
            )
        }

        Text(
            text = dayOfWeek,
            style = getTextStyle(size = 12.sp),
            modifier = GlanceModifier.fillMaxWidth()
        )
    }
}

@Composable
private fun MediumCalendarLayout(calendarData: WidgetCalendarData) {
    val monthName = CalendarUtils.getMonthName(calendarData.month, isShort = false)
    val today = Calendar.getInstance()

    Column(
        modifier = GlanceModifier.fillMaxSize().padding(4.dp)
    ) {
        // Header
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$monthName ${calendarData.year}",
                style = getTextStyle(isHeader = true, size = 16.sp)
            )
        }

        Spacer(modifier = GlanceModifier.height(8.dp))

        // Day headers
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val dayHeaders =
                CalendarUtils.getDayHeaders(isShort = true).map { it.first().toString() }
            dayHeaders.forEach { day ->
                Text(
                    text = day,
                    style = getTextStyle(size = 10.sp),
                    modifier = GlanceModifier.width(20.dp)
                )
            }
        }

        Spacer(modifier = GlanceModifier.height(4.dp))

        // Calendar grid (simplified for medium size)
        CalendarGrid(
            calendarData = calendarData,
            today = today,
            cellSize = 18.dp,
            fontSize = 10.sp
        )
    }
}

@Composable
private fun LargeCalendarLayout(calendarData: WidgetCalendarData) {
    val monthName = CalendarUtils.getMonthName(calendarData.month, isShort = false)
    val today = Calendar.getInstance()

    Column(
        modifier = GlanceModifier.fillMaxSize().padding(8.dp)
    ) {
        // Header with navigation
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$monthName ${calendarData.year}",
                style = getTextStyle(isHeader = true, size = 18.sp)
            )
        }

        Spacer(modifier = GlanceModifier.height(12.dp))

        // Day headers
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val dayHeaders = CalendarUtils.getDayHeaders(isShort = false).map {
                it.take(3) // Take first 3 characters
            }
            dayHeaders.forEach { day ->
                Text(
                    text = day,
                    style = getTextStyle(size = 12.sp),
                    modifier = GlanceModifier.width(32.dp)
                )
            }
        }

        Spacer(modifier = GlanceModifier.height(8.dp))

        // Calendar grid
        CalendarGrid(
            calendarData = calendarData,
            today = today,
            cellSize = 32.dp,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun CalendarGrid(
    calendarData: WidgetCalendarData,
    today: Calendar,
    cellSize: androidx.compose.ui.unit.Dp,
    fontSize: androidx.compose.ui.unit.TextUnit
) {
    val calendarWeeks = CalendarUtils.getCalendarWeeks(calendarData.year, calendarData.month)

    Column {
        calendarWeeks.forEach { week ->
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                week.forEach { day ->
                    CalendarDay(
                        day = day,
                        isToday = day != null && CalendarUtils.isToday(
                            calendarData.year,
                            calendarData.month,
                            day
                        ),
                        isSelected = day == calendarData.selectedDay,
                        cellSize = cellSize,
                        fontSize = fontSize
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarDay(
    day: Int?,
    isToday: Boolean,
    isSelected: Boolean,
    cellSize: androidx.compose.ui.unit.Dp,
    fontSize: androidx.compose.ui.unit.TextUnit
) {
    Box(
        modifier = GlanceModifier
            .size(cellSize)
            .background(
                when {
                    isToday -> Color(0xFF2196F3)
                    isSelected -> Color(0xFF4CAF50)
                    else -> Color.Transparent
                }
            )
            .cornerRadius(4.dp),
        contentAlignment = Alignment.Center
    ) {
        if (day != null) {
            Text(
                text = day.toString(),
                style = getTextStyle(size = fontSize)
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

@SuppressLint("RestrictedApi")
private fun getTextStyle(
    isHeader: Boolean = false,
    size: androidx.compose.ui.unit.TextUnit
): TextStyle {
    return TextStyle(
        color = ColorProvider(Color.White),
        fontSize = size,
        fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
        textAlign = TextAlign.Center
    )
}