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
import androidx.glance.layout.width
import androidx.glance.layout.wrapContentHeight
import androidx.glance.layout.wrapContentWidth
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetSize
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetType
import com.s4ltf1sh.glance_widgets.utils.CalendarWidgetUtils
import com.s4ltf1sh.glance_widgets.utils.selectedDateBackground
import java.util.Calendar

@Composable
fun CalendarType1(
    glanceWidgetSize: GlanceWidgetSize,
    calendar: Calendar,
    onGoToPreviousMonth: () -> Unit,
    onGoToNextMonth: () -> Unit,
) {
    val selectedDateBackground = selectedDateBackground(GlanceWidgetType.Calendar.Type1Glance)

    when (glanceWidgetSize) {
        GlanceWidgetSize.SMALL -> CalendarSmall(
            modifier = GlanceModifier.fillMaxSize().padding(10.dp),
            calendar = calendar,
            onGoToPreviousMonth = onGoToPreviousMonth,
            onGoToNextMonth = onGoToNextMonth,
            selectedDateBackground = selectedDateBackground
        )

        GlanceWidgetSize.MEDIUM -> CalendarMedium(
            modifier = GlanceModifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 18.dp),
            calendar = calendar,
            onGoToPreviousMonth = onGoToPreviousMonth,
            onGoToNextMonth = onGoToNextMonth,
            selectedDateBackground = selectedDateBackground
        )

        GlanceWidgetSize.LARGE -> CalendarLarge(
            modifier = GlanceModifier.fillMaxSize().padding(24.dp),
            calendar = calendar,
            onGoToPreviousMonth = onGoToPreviousMonth,
            onGoToNextMonth = onGoToNextMonth,
            selectedDateBackground = selectedDateBackground
        )
    }
}

@Composable
private fun CalendarSmall(
    modifier: GlanceModifier = GlanceModifier,
    calendar: Calendar,
    onGoToPreviousMonth: () -> Unit,
    onGoToNextMonth: () -> Unit,
    selectedDateBackground: ImageProvider
) {
    val context = LocalContext.current

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CalendarHeaderDefault(
            context = context,
            modifier = GlanceModifier.wrapContentHeight().fillMaxWidth(),
            calendar = calendar,
            textSize = 14.sp,
            textColor = Color.White,
            iconSize = 24.dp,
            iconColor = Color.White,
            onGoToPreviousMonth = onGoToPreviousMonth,
            onGoToNextMonth = onGoToNextMonth
        )

        Spacer(
            modifier = GlanceModifier.height(12.dp)
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
    selectedDateBackground: ImageProvider
) {
    val context = LocalContext.current

    Row(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = GlanceModifier.wrapContentWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CalendarHeaderDefault(
                modifier = GlanceModifier.wrapContentWidth().wrapContentHeight(),
                context = context,
                calendar = Calendar.getInstance(),
                textColor = Color.White,
                textSize = 14.sp,
                iconSize = 20.dp,
                iconColor = Color.White,
                onGoToPreviousMonth = onGoToPreviousMonth,
                onGoToNextMonth = onGoToNextMonth,
                showActionButtons = false
            )

            Spacer(
                modifier = GlanceModifier.height(10.dp)
            )

            CurrentDayWithLocationVertical(
                modifier = GlanceModifier,
                dayOfWeek = CalendarWidgetUtils.getTodayDayOfWeek(),
                dayOfWeekSize = 16.sp,
                dayOfWeekColor = Color.White,
                dayOfMonth = CalendarWidgetUtils.getTodayDayOfMonth(),
                dayOfMonthSize = 32.sp,
                dayOfMonthColor = Color.White,
                location = "USA, New York",
                locationSize = 11.sp
            )
        }

        Spacer(
            modifier = GlanceModifier.width(20.dp)
        )

        DatesDefault(
            modifier = GlanceModifier.fillMaxSize(),
            calendar = calendar,
            dateTextSize = 12.sp,
            focusedDateColor = Color.White,
            showUnfocusedDates = false,
            selectedDateBackground = selectedDateBackground
        )
    }
}

@Composable
private fun CalendarLarge(
    modifier: GlanceModifier = GlanceModifier,
    calendar: Calendar,
    onGoToPreviousMonth: () -> Unit,
    onGoToNextMonth: () -> Unit,
    selectedDateBackground: ImageProvider
) {
    val monthName = CalendarWidgetUtils.getCurrentMonthName()
    val year = CalendarWidgetUtils.getCurrentYear()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CurrentDayWithLocationHorizontal(
            modifier = GlanceModifier.fillMaxWidth().wrapContentHeight(),
            dayOfWeek = CalendarWidgetUtils.getTodayDayOfWeek(),
            dayOfWeekSize = 14.sp,
            dayOfWeekColor = Color.White,
            dayOfMonth = CalendarWidgetUtils.getTodayDayOfMonth(),
            dayOfMonthSize = 48.sp,
            dayOfMonthColor = Color.White,
            monthName = monthName,
            monthNameSize = 18.sp,
            monthNameColor = Color.White,
            year = year,
            yearSize = 18.sp,
            location = "USA, New York",
            locationSize = 11.sp
        )

        Spacer(modifier = GlanceModifier.height(20.dp))

        DatesWithMonthButtons(
            calendar = calendar,
            dateTextSize = 12.sp,
            dateTextColor = Color.White,
            selectedDateColor = Color.White,
            selectedDateBackground = selectedDateBackground,
            monthButtonColor = Color.White,
            monthButtonBackground = Color.White.copy(alpha = 0.2F),
            onDateClick = null,
            onNextMonthClick = onGoToNextMonth,
            onPreviousMonthClick = onGoToPreviousMonth
        )
    }
}