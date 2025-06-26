package com.s4ltf1sh.glance_widgets.widget.widget.calendar.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import com.s4ltf1sh.glance_widgets.model.WidgetSize
import com.s4ltf1sh.glance_widgets.ui.theme.MonthCalendarColors
import java.util.Calendar

@Composable
fun CalendarType1(
    widgetSize: WidgetSize,
    calendar: Calendar,
    onGoToPreviousMonth: () -> Unit,
    onGoToNextMonth: () -> Unit,
    dayOfWeekNames: List<String> = listOf(
        "Sun",
        "Mon",
        "Tue",
        "Wed",
        "Thu",
        "Fri",
        "Sat"
    ),
    monthCalendarColors: MonthCalendarColors,
    selectedDateBackground: (() -> ImageProvider)? = null
) {
    when (widgetSize) {
        WidgetSize.SMALL -> CalendarSmall(
            modifier = GlanceModifier.padding(20.dp),
            calendar = calendar,
            onGoToPreviousMonth = onGoToPreviousMonth,
            onGoToNextMonth = onGoToNextMonth,
            selectedDateBackground = selectedDateBackground
        )

        WidgetSize.MEDIUM -> CalendarMedium(
            modifier = GlanceModifier.padding(24.dp),
            calendar = calendar,
            onGoToPreviousMonth = onGoToPreviousMonth,
            onGoToNextMonth = onGoToNextMonth,
            selectedDateBackground = selectedDateBackground
        )

        WidgetSize.LARGE -> TODO()
    }
}

@Composable
private fun CalendarSmall(
    modifier: GlanceModifier = GlanceModifier,
    calendar: Calendar,
    onGoToPreviousMonth: () -> Unit,
    onGoToNextMonth: () -> Unit,
    selectedDateBackground: (() -> ImageProvider)? = null
) {
    val context = LocalContext.current

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CalendarHeaderDefault(
            context = context,
            modifier = GlanceModifier.fillMaxWidth(),
            calendar = calendar,
            textSize = 12.sp,
            textColor = Color.White,
            iconSize = 14.dp,
            iconColor = Color.White,
            onGoToPreviousMonth = onGoToPreviousMonth,
            onGoToNextMonth = onGoToNextMonth
        )

        Spacer(
            modifier = GlanceModifier.height(20.dp)
        )

        DatesDefault(
            modifier = GlanceModifier.fillMaxSize(),
            calendar = calendar,
            dateTextSize = 10.sp,
            focusedDateColor = Color.White,
            showUnfocusedDates = false,
            selectedDateBackground = selectedDateBackground
        )
    }
}

@Composable
private fun CalendarMedium(
    modifier: GlanceModifier = GlanceModifier,
    calendar: Calendar,
    onGoToPreviousMonth: () -> Unit,
    onGoToNextMonth: () -> Unit,
    selectedDateBackground: (() -> ImageProvider)? = null
) {
    val context = LocalContext.current

    Row(
        modifier = modifier
    ) {
        CalendarHeaderDefault(
            context = context,
            calendar = calendar,
            textColor = Color.White,
            textSize = 14.sp,
            iconSize = 20.dp,
            iconColor = Color.White,
            onGoToPreviousMonth = onGoToPreviousMonth,
            onGoToNextMonth = onGoToNextMonth
        )

        Spacer(
            modifier = GlanceModifier.height(20.dp)
        )

        DatesDefault(
            modifier = GlanceModifier.fillMaxSize(),
            calendar = calendar,
            dateTextSize = 10.sp,
            focusedDateColor = Color.White,
            showUnfocusedDates = false,
            selectedDateBackground = selectedDateBackground
        )
    }
}