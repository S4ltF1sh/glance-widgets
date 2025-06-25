package com.s4ltf1sh.glance_widgets.widget.component

import android.content.Context
import android.graphics.drawable.Icon
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.RowScope
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.wrapContentSize
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.s4ltf1sh.glance_widgets.R
import com.s4ltf1sh.glance_widgets.ui.theme.Dimens
import com.s4ltf1sh.glance_widgets.ui.theme.MonthCalendarColors
import com.s4ltf1sh.glance_widgets.utils.MonthCalendarWidgetUtils
import java.util.Calendar

@Composable
fun MonthCalendarView(
    modifier: GlanceModifier = GlanceModifier,
    calendar: Calendar,
    onGoToPreviousMonth: () -> Unit,
    onGoToNextMonth: () -> Unit,
    onDateClick: Action,
    dayOfWeekNames: List<String> = listOf(
        "Sun",
        "Mon",
        "Tue",
        "Wed",
        "Thu",
        "Fri",
        "Sat"
    ), // Default English names
    monthCalendarColors: MonthCalendarColors,
    selectedDateBackground: (() -> ImageProvider)? = null
) {
    val context = LocalContext.current

    Column(modifier = modifier.background(monthCalendarColors.background)) {
        MonthCalendarHeaderView(
            context = context,
            calendar = calendar,
            monthCalendarColors = monthCalendarColors,
            onGoToPreviousMonth = onGoToPreviousMonth,
            onGoToNextMonth = onGoToNextMonth
        )
        DaysOfWeek(
            monthCalendarColors = monthCalendarColors,
            dayOfWeekNames = dayOfWeekNames
        )
        Date(
            calendar = calendar,
            monthCalendarColors = monthCalendarColors,
            onDateClick = onDateClick,
            selectedDateBackground = selectedDateBackground
        )
    }
}

@Composable
private fun MonthCalendarHeaderView(
    context: Context,
    calendar: Calendar,
    onGoToPreviousMonth: () -> Unit,
    onGoToNextMonth: () -> Unit,
    monthCalendarColors: MonthCalendarColors
) {
    Row(
        modifier = GlanceModifier.fillMaxWidth().padding(vertical = Dimens.defaultPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val leftIcon = Icon.createWithResource(context, R.drawable.ic_widget_arrow_left)
        leftIcon.setTint(monthCalendarColors.iconColor.getColor(context).toArgb())

        val rightIcon = Icon.createWithResource(context, R.drawable.ic_widget_arrow_right)
        rightIcon.setTint(monthCalendarColors.iconColor.getColor(context).toArgb())

        Image(
            modifier = getHeaderImageModifier(onGoToPreviousMonth),
            provider = ImageProvider(leftIcon),
            contentDescription = getResString(context, R.string.desc_previous_month)
        )

        Text(
            text = getFormatString(
                calendar.timeInMillis,
                MonthCalendarWidgetUtils.MONTH_YEAR_FORMAT
            ),
            style = TextStyle(
                fontSize = Dimens.mediumFontSize,
                textAlign = TextAlign.Center,
                color = monthCalendarColors.monthYearHeaderColor
            )
        )

        Image(
            modifier = getHeaderImageModifier(onGoToNextMonth),
            provider = ImageProvider(rightIcon),
            contentDescription = getResString(context, R.string.desc_next_month)
        )
    }
}

private fun getResString(context: Context, stringResId: Int): String {
    return context.resources.getString(stringResId)
}

private fun getFormatString(timeInMillis: Long, requiredFormat: String): String {
    return MonthCalendarWidgetUtils.formatDateTime(
        timeInMillis = timeInMillis,
        requiredFormat = requiredFormat
    )
}

@Composable
private fun DaysOfWeek(
    monthCalendarColors: MonthCalendarColors,
    dayOfWeekNames: List<String>
) {
    // Đảm bảo dayOfWeekNames có đủ 7 phần tử và luôn theo thứ tự Sunday first
    val safeWeekNames = if (dayOfWeekNames.size >= 7) {
        dayOfWeekNames.take(7)
    } else {
        // Fallback to default English names if invalid
        listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    }

    Row(modifier = GlanceModifier.fillMaxWidth()) {
        // Hiển thị theo thứ tự cố định: Sunday -> Monday -> ... -> Saturday
        for (i in 0 until MonthCalendarWidgetUtils.COLUMN_COUNT) {
            Text(
                modifier = GlanceModifier.defaultWeight(),
                text = safeWeekNames[i], // Index 0=Sunday, 1=Monday, ..., 6=Saturday
                style = TextStyle(
                    color = monthCalendarColors.weekDayTextColor,
                    fontSize = Dimens.mediumFontSize,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}

@Composable
private fun Date(
    calendar: Calendar,
    monthCalendarColors: MonthCalendarColors,
    onDateClick: Action,
    selectedDateBackground: (() -> ImageProvider)? = null
) {
    val todayCalendar = MonthCalendarWidgetUtils.getCalendar()
    val dateCalendar = calendar.clone() as Calendar
    dateCalendar.set(Calendar.DAY_OF_MONTH, 1)
    MonthCalendarWidgetUtils.setTimeToBeginningOfDay(dateCalendar)

    val currentMonthInt = dateCalendar.get(Calendar.MONTH)
    val currentYear = dateCalendar.get(Calendar.YEAR)

    // Tính offset của ngày đầu tháng so với Sunday (0=Sunday, 1=Monday, ...)
    val firstDayOfMonthOffset = (dateCalendar.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY + 7) % 7

    // Lấy số ngày của tháng hiện tại
    val daysInCurrentMonth = dateCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    // Tính số ngày của tháng trước
    val prevMonthCalendar = dateCalendar.clone() as Calendar
    prevMonthCalendar.add(Calendar.MONTH, -1)
    val daysInPrevMonth = prevMonthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val prevMonthInt = prevMonthCalendar.get(Calendar.MONTH)
    val prevYear = prevMonthCalendar.get(Calendar.YEAR)

    // Tháng sau
    val nextMonthCalendar = dateCalendar.clone() as Calendar
    nextMonthCalendar.add(Calendar.MONTH, 1)
    val nextMonthInt = nextMonthCalendar.get(Calendar.MONTH)
    val nextYear = nextMonthCalendar.get(Calendar.YEAR)

    // Tạo array để chứa tất cả các ngày cần hiển thị (42 ô = 6 tuần × 7 ngày)
    val totalCells = MonthCalendarWidgetUtils.ROW_COUNT * MonthCalendarWidgetUtils.COLUMN_COUNT
    val dateItems = Array<DateItem?>(totalCells) { null }

    // Fill các ngày của tháng trước (unfocused)
    if (firstDayOfMonthOffset > 0) {
        val startDay = daysInPrevMonth - firstDayOfMonthOffset + 1
        for (i in 0 until firstDayOfMonthOffset) {
            val day = startDay + i
            val dayCalendar = prevMonthCalendar.clone() as Calendar
            dayCalendar.set(Calendar.DAY_OF_MONTH, day)

            dateItems[i] = DateItem(
                dayString = day.toString(),
                calendar = dayCalendar,
                isCurrentMonth = false
            )
        }
    }

    // Fill các ngày của tháng hiện tại (focused)
    for (day in 1..daysInCurrentMonth) {
        val position = firstDayOfMonthOffset + day - 1
        if (position < totalCells) {
            val dayCalendar = dateCalendar.clone() as Calendar
            dayCalendar.set(Calendar.DAY_OF_MONTH, day)

            dateItems[position] = DateItem(
                dayString = day.toString(),
                calendar = dayCalendar,
                isCurrentMonth = true
            )
        }
    }

    // Fill các ngày của tháng sau (unfocused)
    val remainingCells = totalCells - (firstDayOfMonthOffset + daysInCurrentMonth)
    if (remainingCells > 0) {
        val startPosition = firstDayOfMonthOffset + daysInCurrentMonth
        for (i in 0 until remainingCells) {
            val day = i + 1
            val dayCalendar = nextMonthCalendar.clone() as Calendar
            dayCalendar.set(Calendar.DAY_OF_MONTH, day)

            dateItems[startPosition + i] = DateItem(
                dayString = day.toString(),
                calendar = dayCalendar,
                isCurrentMonth = false
            )
        }
    }

    Column(modifier = GlanceModifier.fillMaxSize().padding(top = Dimens.padding10)) {
        for (row in 0 until MonthCalendarWidgetUtils.ROW_COUNT) {
            Row(
                modifier = GlanceModifier.fillMaxWidth().defaultWeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (column in 0 until MonthCalendarWidgetUtils.COLUMN_COUNT) {
                    val position = row * MonthCalendarWidgetUtils.COLUMN_COUNT + column
                    val dateItem = dateItems[position]

                    DateTextView(
                        dateString = dateItem?.dayString ?: "",
                        isToday = dateItem?.let {
                            MonthCalendarWidgetUtils.isSameDay(it.calendar, todayCalendar)
                        } == true,
                        isCurrentMonth = dateItem?.isCurrentMonth == true,
                        onClick = onDateClick,
                        monthCalendarColors = monthCalendarColors,
                        selectedDateBackground = selectedDateBackground
                    )
                }
            }
        }
    }
}

private data class DateItem(
    val dayString: String,
    val calendar: Calendar,
    val isCurrentMonth: Boolean
)

@Composable
private fun RowScope.DateTextView(
    dateString: String,
    isToday: Boolean,
    isCurrentMonth: Boolean,
    onClick: Action,
    monthCalendarColors: MonthCalendarColors,
    selectedDateBackground: (() -> ImageProvider)? = null
) {
    val hasDate = dateString.isNotEmpty()

    Box(
        modifier = getDateTextParentModifier(hasDate, onClick),
        contentAlignment = Alignment.Center
    ) {
        // Background cho selected date
        Log.d("DateTextView", "isToday: $isToday, hasDate: $hasDate, selectedDateBackground: $selectedDateBackground")
        if (isToday && hasDate && selectedDateBackground != null) {
            Image(
                provider = selectedDateBackground(),
                contentDescription = null,
                modifier = GlanceModifier.fillMaxSize()
            )
        }

        Text(
            modifier = getDateTextChildModifier(hasDate)
                .padding(Dimens.padding2),
            text = dateString,
            style = TextStyle(
                color = getDateTextColor(
                    isToday = isToday,
                    isCurrentMonth = isCurrentMonth,
                    monthCalendarColors = monthCalendarColors
                ),
                fontSize = Dimens.smallFontSize,
                textAlign = TextAlign.Center
            ),
            maxLines = 1
        )
    }
}

private fun getDateTextColor(
    isToday: Boolean,
    isCurrentMonth: Boolean,
    monthCalendarColors: MonthCalendarColors
): ColorProvider {
    return when {
        isToday -> monthCalendarColors.todayDateTextColor
        isCurrentMonth -> monthCalendarColors.dateTextColor
        else -> monthCalendarColors.unfocusedDateTextColor // Tháng trước/sau
    }
}

private fun RowScope.getDateTextParentModifier(
    isCurrentMonth: Boolean,
    onClick: Action
): GlanceModifier {
    return if (isCurrentMonth) {
        GlanceModifier.defaultWeight().clickable(onClick = onClick)
    } else {
        GlanceModifier.defaultWeight()
    }
}

private fun getDateTextChildModifier(isCurrentMonth: Boolean):
        GlanceModifier {
    return if (isCurrentMonth) {
        GlanceModifier.wrapContentSize()
    } else {
        GlanceModifier.wrapContentSize()
    }
}

@Composable
private fun getHeaderImageModifier(onClick: () -> Unit): GlanceModifier {
    return GlanceModifier.size(Dimens.imageSize)
        .clickable {
            onClick()
        }.padding(
            vertical = Dimens.defaultHalfPadding,
            horizontal = Dimens.defaultHalfPadding
        )
}

private fun getArrowLeftIcon(context: Context): ImageProvider {
    val icon = Icon.createWithResource(context, R.drawable.ic_widget_arrow_left)
    return ImageProvider(icon)
}

private fun getArrowRightIcon(context: Context): ImageProvider {
    val icon = Icon.createWithResource(context, R.drawable.ic_widget_arrow_right)
    return ImageProvider(icon)
}

private fun getImageProvider(drawableResId: Int) = ImageProvider(drawableResId)