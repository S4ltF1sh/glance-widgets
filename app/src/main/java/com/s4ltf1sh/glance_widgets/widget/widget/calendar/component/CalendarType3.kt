package com.s4ltf1sh.glance_widgets.widget.widget.calendar.component

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.s4ltf1sh.glance_widgets.R
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetSize
import com.s4ltf1sh.glance_widgets.utils.CalendarWidgetUtils
import com.s4ltf1sh.glance_widgets.utils.toColor
import java.util.Calendar

@Composable
fun CalendarType3(
    glanceWidgetSize: GlanceWidgetSize,
    calendar: Calendar,
    dayOfWeekNames: List<String>,
) {
    val selectedDateBackground = ImageProvider(R.drawable.calendar_selected_bg_3)

    when (glanceWidgetSize) {
        GlanceWidgetSize.SMALL -> CalendarSmall(
            modifier = GlanceModifier.fillMaxSize().padding(6.dp),
            calendar = calendar,
            dayOfWeekNames = dayOfWeekNames,
            selectedDateBackground = selectedDateBackground
        )

        GlanceWidgetSize.MEDIUM -> CalendarMedium(
            modifier = GlanceModifier.fillMaxSize().padding(vertical = 4.dp, horizontal = 64.dp),
            calendar = calendar,
            dayOfWeekNames = dayOfWeekNames,
            selectedDateBackground = selectedDateBackground
        )

        GlanceWidgetSize.LARGE -> CalendarLarge(
            modifier = GlanceModifier.fillMaxSize().padding(12.dp),
            calendar = calendar,
            dayOfWeekNames = dayOfWeekNames,
            selectedDateBackground = selectedDateBackground
        )
    }
}

@Composable
private fun CalendarSmall(
    modifier: GlanceModifier = GlanceModifier.fillMaxSize(),
    calendar: Calendar,
    dayOfWeekNames: List<String>,
    selectedDateBackground: ImageProvider
) {
    val monthAndYear = CalendarWidgetUtils.getCurrentMonthAndYear().uppercase()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Header(
            modifier = GlanceModifier.fillMaxWidth().padding(horizontal = 8.dp),
            monthAndYearMonth = monthAndYear,
            textSize = 14.sp,
            textColor = Color.White
        )

        Spacer(modifier = GlanceModifier.height(4.dp))

        DaysOfWeek(
            textColor = "#FF9330".toColor(),
            textSize = 9.sp,
            dayOfWeekNames = dayOfWeekNames
        )

        DatesDefault(
            modifier = GlanceModifier.fillMaxSize(),
            calendar = calendar,
            dateTextSize = 11.sp,
            focusedDateColor = Color.White,
            unfocusedDateColor = Color.White.copy(0.41F),
            selectedDateColor = Color.White,
            selectedDateBackground = selectedDateBackground,
            showUnfocusedDates = true
        )
    }
}

@Composable
private fun CalendarMedium(
    modifier: GlanceModifier = GlanceModifier.fillMaxSize(),
    calendar: Calendar,
    dayOfWeekNames: List<String>,
    selectedDateBackground: ImageProvider
) {
    val monthAndYear = CalendarWidgetUtils.getCurrentMonthAndYear()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Header(
            modifier = GlanceModifier.fillMaxWidth().padding(horizontal = 12.dp),
            monthAndYearMonth = monthAndYear,
            textSize = 14.sp,
            textColor = Color.White
        )

        Spacer(modifier = GlanceModifier.height(4.dp))

        DaysOfWeek(
            textColor = "#FF9330".toColor(),
            textSize = 9.sp,
            dayOfWeekNames = dayOfWeekNames
        )

        DatesDefault(
            modifier = GlanceModifier.fillMaxSize(),
            calendar = calendar,
            dateTextSize = 11.sp,
            focusedDateColor = Color.White,
            unfocusedDateColor = Color.White.copy(0.41F),
            selectedDateColor = Color.White,
            selectedDateBackground = selectedDateBackground,
            showUnfocusedDates = true
        )
    }
}

@Composable
private fun CalendarLarge(
    modifier: GlanceModifier = GlanceModifier.fillMaxSize(),
    calendar: Calendar,
    dayOfWeekNames: List<String>,
    selectedDateBackground: ImageProvider
) {
    val monthAndYear = CalendarWidgetUtils.getCurrentMonthAndYear()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Header(
            modifier = GlanceModifier.fillMaxWidth().padding(horizontal = 12.dp),
            monthAndYearMonth = monthAndYear,
            textSize = 16.sp,
            textColor = Color.White
        )

        Spacer(modifier = GlanceModifier.height(6.dp))

        DaysOfWeek(
            textColor = "#FF9330".toColor(),
            textSize = 14.sp,
            dayOfWeekNames = dayOfWeekNames
        )

        DatesDefault(
            modifier = GlanceModifier.fillMaxSize(),
            calendar = calendar,
            dateTextSize = 12.sp,
            focusedDateColor = Color.White,
            unfocusedDateColor = Color.White.copy(0.41F),
            selectedDateColor = Color.White,
            selectedDateBackground = selectedDateBackground,
            showUnfocusedDates = true
        )
    }
}

@SuppressLint("RestrictedApi")
@Composable
private fun Header(
    modifier: GlanceModifier = GlanceModifier,
    monthAndYearMonth: String,
    textSize: TextUnit,
    textColor: Color
) {
    Text(
        modifier = modifier,
        text = monthAndYearMonth,
        style = TextStyle(
            fontSize = textSize,
            color = ColorProvider(textColor),
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.Medium
        )
    )
}