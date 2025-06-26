package com.s4ltf1sh.glance_widgets.widget.widget.calendar.component

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.s4ltf1sh.glance_widgets.utils.CalendarWidgetUtils
import java.util.Calendar

@SuppressLint("RestrictedApi")
@Composable
private fun Header(
    modifier: GlanceModifier = GlanceModifier,
    calendar: Calendar,
    dayOfWeekSize: TextUnit,
    dayOfWeekColor: Color,
    monthYearSize: TextUnit,
    monthYearColor: Color,
    iconSize: Dp,
    iconColor: Color,
    onGoToPreviousMonth: () -> Unit,
    onGoToNextMonth: () -> Unit
) {
    Text(
        modifier = modifier,
        text = CalendarWidgetUtils.getTodayDayOfWeek(),
        style = TextStyle(
            fontSize = dayOfWeekSize,
            color = ColorProvider(Color.White),
            textAlign = TextAlign.Start
        )
    )

    Spacer(modifier = GlanceModifier.fillMaxWidth())

    CalendarHeaderDefault(
        context = LocalContext.current,
        calendar = calendar,
        textSize = monthYearSize,
        textColor = Color.White,
        iconSize = iconSize,
        iconColor = iconColor,
        onGoToPreviousMonth = onGoToPreviousMonth,
        onGoToNextMonth = onGoToNextMonth
    )
}