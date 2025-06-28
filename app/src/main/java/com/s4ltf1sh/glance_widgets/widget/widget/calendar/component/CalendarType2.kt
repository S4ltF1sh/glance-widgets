package com.s4ltf1sh.glance_widgets.widget.widget.calendar.component

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.s4ltf1sh.glance_widgets.R
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetSize
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetType
import com.s4ltf1sh.glance_widgets.utils.CalendarWidgetUtils
import com.s4ltf1sh.glance_widgets.utils.selectedDateBackground
import com.s4ltf1sh.glance_widgets.utils.toColor
import com.s4ltf1sh.glance_widgets.widget.component.GlanceIcon
import java.util.Calendar

@Composable
fun CalendarType2(
    glanceWidgetSize: GlanceWidgetSize,
    calendar: Calendar,
    dayOfWeekNames: List<String>,
    onGoToPreviousMonth: () -> Unit,
    onGoToNextMonth: () -> Unit,
) {
    val selectedDateBackground = selectedDateBackground(GlanceWidgetType.Calendar.Type2Glance)

    when(glanceWidgetSize) {
        GlanceWidgetSize.SMALL -> CalendarSmall(
            calendar = calendar,
            dayOfWeekNames = dayOfWeekNames,
            onGoToPreviousMonth = onGoToPreviousMonth,
            onGoToNextMonth = onGoToNextMonth,
            selectedDateBackground = selectedDateBackground
        )

        GlanceWidgetSize.MEDIUM -> CalendarMedium(
            calendar = calendar,
            dayOfWeekNames = dayOfWeekNames,
            onGoToPreviousMonth = onGoToPreviousMonth,
            onGoToNextMonth = onGoToNextMonth,
            selectedDateBackground = selectedDateBackground
        )

        GlanceWidgetSize.LARGE -> CalendarLarge(
            calendar = calendar,
            dayOfWeekNames = dayOfWeekNames,
            onGoToPreviousMonth = onGoToPreviousMonth,
            onGoToNextMonth = onGoToNextMonth,
            selectedDateBackground = selectedDateBackground
        )
    }
}

@Composable
private fun CalendarSmall(
    modifier: GlanceModifier = GlanceModifier.fillMaxSize(),
    calendar: Calendar,
    dayOfWeekNames: List<String>,
    onGoToPreviousMonth: () -> Unit,
    onGoToNextMonth: () -> Unit,
    selectedDateBackground: ImageProvider
) {
    val monthAndYear = CalendarWidgetUtils.getCurrentMonthAndYear()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Header(
            modifier = GlanceModifier.fillMaxWidth(),
            monthAndYearMonth = monthAndYear,
            textSize = 10f,
            textColor = Color(0xFFFCFDB2),
            iconSize = 10.dp,
            iconColor = Color(0xFF0A84FF),
            onGoToPreviousMonth = onGoToPreviousMonth,
            onGoToNextMonth = onGoToNextMonth
        )

        DaysOfWeek(
            textColor = "#EBEBF580".toColor(),
            textSize = 6.sp,
            dayOfWeekNames = dayOfWeekNames
        )

        DatesDefault(
            calendar = calendar,
            dateTextSize = 10.sp,
            focusedDateColor = "#FCFDB2".toColor(),
            selectedDateBackground = selectedDateBackground,
            showUnfocusedDates = false
        )
    }
}

@Composable
private fun CalendarMedium(
    modifier: GlanceModifier = GlanceModifier.fillMaxSize(),
    calendar: Calendar,
    dayOfWeekNames: List<String>,
    onGoToPreviousMonth: () -> Unit,
    onGoToNextMonth: () -> Unit,
    selectedDateBackground: ImageProvider
) {
    val monthAndYear = CalendarWidgetUtils.getCurrentMonthAndYear()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Header(
            modifier = GlanceModifier.fillMaxWidth(),
            monthAndYearMonth = monthAndYear,
            textSize = 10f,
            textColor = Color(0xFFFCFDB2),
            iconSize = 10.dp,
            iconColor = Color(0xFF0A84FF),
            onGoToPreviousMonth = onGoToPreviousMonth,
            onGoToNextMonth = onGoToNextMonth
        )

        DaysOfWeek(
            textColor = "#EBEBF580".toColor(),
            textSize = 6.sp,
            dayOfWeekNames = dayOfWeekNames
        )

        DatesDefault(
            calendar = calendar,
            dateTextSize = 10.sp,
            focusedDateColor = "#FCFDB2".toColor(),
            selectedDateBackground = selectedDateBackground,
            showUnfocusedDates = false
        )
    }
}

@Composable
private fun CalendarLarge(
    modifier: GlanceModifier = GlanceModifier.fillMaxSize(),
    calendar: Calendar,
    dayOfWeekNames: List<String>,
    onGoToPreviousMonth: () -> Unit,
    onGoToNextMonth: () -> Unit,
    selectedDateBackground: ImageProvider
) {
    val monthAndYear = CalendarWidgetUtils.getCurrentMonthAndYear()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Header(
            modifier = GlanceModifier.fillMaxWidth(),
            monthAndYearMonth = monthAndYear,
            textSize = 16f,
            textColor = Color(0xFFFCFDB2),
            iconSize = 20.dp,
            iconColor = Color(0xFF0A84FF),
            onGoToPreviousMonth = onGoToPreviousMonth,
            onGoToNextMonth = onGoToNextMonth
        )

        DaysOfWeek(
            textColor = "#EBEBF580".toColor(),
            textSize = 12.sp,
            dayOfWeekNames = dayOfWeekNames
        )

        DatesDefault(
            calendar = calendar,
            dateTextSize = 20.sp,
            focusedDateColor = "#FCFDB2".toColor(),
            selectedDateBackground = selectedDateBackground,
            showUnfocusedDates = false
        )
    }
}

@SuppressLint("RestrictedApi")
@Composable
private fun Header(
    modifier: GlanceModifier = GlanceModifier,
    monthAndYearMonth: String,
    textSize: Float,
    textColor: Color,
    iconSize: Dp,
    iconColor: Color,
    onGoToPreviousMonth: () -> Unit,
    onGoToNextMonth: () -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = monthAndYearMonth,
            style = TextStyle(
                fontSize = TextUnit(textSize, TextUnitType.Sp),
                color = ColorProvider(textColor)
            )
        )

        GlanceIcon(
            resId = R.drawable.ic_widget_arrow_right,
            tint = "#0A84FF".toColor(),
            size = textSize.dp
        )

        Spacer(
            modifier = GlanceModifier.fillMaxWidth()
        )

        GlanceIcon(
            resId = R.drawable.ic_widget_arrow_left,
            tint = iconColor,
            size = iconSize,
            onClick = onGoToPreviousMonth
        )

        GlanceIcon(
            resId = R.drawable.ic_widget_arrow_right,
            tint = iconColor,
            size = iconSize,
            onClick = onGoToNextMonth
        )
    }
}