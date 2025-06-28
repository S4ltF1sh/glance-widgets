package com.s4ltf1sh.glance_widgets.widget.widget.calendar.component

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.s4ltf1sh.glance_widgets.R
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetSize
import com.s4ltf1sh.glance_widgets.ui.theme.Dimens
import com.s4ltf1sh.glance_widgets.utils.CalendarWidgetUtils
import com.s4ltf1sh.glance_widgets.utils.getDayOfWeekName
import java.util.Calendar

@Composable
fun CalendarType5(
    glanceWidgetSize: GlanceWidgetSize,
    calendar: Calendar,
    dayOfWeekNames: List<String>
) {
    val selectedDateBackground = ImageProvider(R.drawable.calendar_selected_bg_5)
    when (glanceWidgetSize) {
        GlanceWidgetSize.SMALL -> CalendarSmall(Calendar.getInstance())
        GlanceWidgetSize.MEDIUM -> CalendarMedium(
            calendar = Calendar.getInstance(),
            dayOfWeekNames = dayOfWeekNames
        )
        GlanceWidgetSize.LARGE -> CalendarLarge(
            calendar = Calendar.getInstance(),
            dayOfWeekNames = dayOfWeekNames,
            selectedDateBackground = selectedDateBackground
        )
    }
}

@Composable
private fun CalendarSmall(
    calendar: Calendar
) {
    CalendarSingleDayView(
        modifier = GlanceModifier.fillMaxSize().padding(vertical = 10.dp),
        calendar = calendar,
        spaceBetween = 10.dp,
        monthAndYearTextSize = 14.sp,
        monthAndYearTextColor = Color.White,
        dayOfWeek = calendar.getDayOfWeekName(),
        dayOfWeekTextSize = 24.sp,
        dayOfWeekTextColor = Color.White,
        dayOfMonthTextSize = 64.sp,
        datOfMonthTextColor = Color.White
    )
}

@Composable
private fun CalendarMedium(
    calendar: Calendar,
    modifier: GlanceModifier = GlanceModifier.fillMaxSize(),
    dayOfWeekNames: List<String>
) {
    Row(
        modifier = modifier.padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CalendarSingleDayView(
            modifier = GlanceModifier.fillMaxSize(),
            calendar = calendar,
            spaceBetween = 10.dp,
            monthAndYearTextSize = 16.sp,
            monthAndYearTextColor = Color.White,
            dayOfWeek = calendar.getDayOfWeekName(),
            dayOfWeekTextSize = 24.sp,
            dayOfWeekTextColor = Color.White,
            dayOfMonthTextSize = 64.sp,
            datOfMonthTextColor = Color.White
        )

        Spacer(
            modifier = GlanceModifier.width(20.dp)
        )

        Spacer(
            modifier = GlanceModifier.width(0.5.dp).background(Color.White)
        )

        Spacer(
            modifier = GlanceModifier.width(20.dp)
        )

        Column(
            modifier = GlanceModifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DaysOfWeek(
                textColor = Color.White,
                textSize = 12.sp,
                dayOfWeekNames = dayOfWeekNames
            )

            Spacer(modifier = GlanceModifier.height(10.dp))

            DatesDefault(
                modifier = GlanceModifier.fillMaxSize(),
                calendar = calendar,
                dateTextSize = 12.sp,
                focusedDateColor = Color.White,
                showUnfocusedDates = false,
                selectedDateColor = Color.White,
                selectedDateBackground = ImageProvider(R.drawable.calendar_selected_bg_5)
            )
        }
    }
}


@Composable
private fun CalendarLarge(
    modifier: GlanceModifier = GlanceModifier.fillMaxSize(),
    calendar: Calendar,
    dayOfWeekNames: List<String>,
    selectedDateBackground: ImageProvider
) {
    Column(
        modifier = modifier.padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LargeWidgetHeader(
            modifier = GlanceModifier.fillMaxWidth(),
            calendar = calendar,
            dayOfWeekSize = 28.sp,
            dayOfWeekColor = Color.White,
            monthYearSize = 20.sp,
            monthYearColor = Color.White
        )

        DaysOfWeek(
            textColor = Color.White,
            textSize = 12.sp,
            dayOfWeekNames = dayOfWeekNames
        )

        DatesDefault(
            calendar = calendar,
            dateTextSize = 20.sp,
            focusedDateColor = Color.White,
            selectedDateBackground = selectedDateBackground,
            showUnfocusedDates = false
        )
    }
}

@SuppressLint("RestrictedApi")
@Composable
private fun LargeWidgetHeader(
    modifier: GlanceModifier = GlanceModifier,
    calendar: Calendar,
    dayOfWeekSize: TextUnit,
    dayOfWeekColor: Color,
    monthYearSize: TextUnit,
    monthYearColor: Color
) {
    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            modifier = modifier,
            text = CalendarWidgetUtils.getTodayDayOfWeek(),
            style = TextStyle(
                fontSize = dayOfWeekSize,
                color = ColorProvider(dayOfWeekColor),
                textAlign = TextAlign.Start
            )
        )

        Spacer(modifier = GlanceModifier.fillMaxWidth())

        CalendarHeaderDefault(
            context = LocalContext.current,
            calendar = calendar,
            textSize = monthYearSize,
            textColor = monthYearColor,
            iconSize = 20.dp,
            iconColor = Color.White,
            showActionButtons = false
        )
    }
}

@SuppressLint("RestrictedApi")
@Composable
private fun CalendarSingleDayView(
    modifier: GlanceModifier = GlanceModifier.fillMaxSize(),
    calendar: Calendar,
    spaceBetween: Dp = 10.dp,
    monthAndYearTextSize: TextUnit,
    monthAndYearTextColor: Color,
    dayOfWeek: String,
    dayOfWeekTextSize: TextUnit,
    dayOfWeekTextColor: Color,
    dayOfMonthTextSize: TextUnit,
    datOfMonthTextColor: Color
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = dayOfWeek,
            style = TextStyle(
                fontSize = dayOfWeekTextSize,
                textAlign = TextAlign.Center,
                color = ColorProvider(dayOfWeekTextColor)
            ),
            modifier = GlanceModifier.fillMaxWidth().padding(Dimens.defaultPadding)
        )

        Spacer(modifier = GlanceModifier.height(spaceBetween))

        CalendarHeaderDefault(
            context = LocalContext.current,
            calendar = calendar,
            textColor = monthAndYearTextColor,
            textSize = monthAndYearTextSize,
            iconSize = 20.dp,
            iconColor = Color.White,
            showActionButtons = false
        )

        Spacer(modifier = GlanceModifier.height(spaceBetween))

        Text(
            text = CalendarWidgetUtils.getTodayDayOfMonth().toString(),
            style = TextStyle(
                fontSize = dayOfMonthTextSize,
                textAlign = TextAlign.Center,
                color = ColorProvider(datOfMonthTextColor)
            ),
            modifier = GlanceModifier.fillMaxWidth().padding(Dimens.defaultPadding)
        )
    }
}