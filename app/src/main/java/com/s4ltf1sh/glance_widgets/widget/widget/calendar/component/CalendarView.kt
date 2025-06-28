package com.s4ltf1sh.glance_widgets.widget.widget.calendar.component

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.layout.wrapContentHeight
import androidx.glance.layout.wrapContentSize
import androidx.glance.layout.wrapContentWidth
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.s4ltf1sh.glance_widgets.R
import com.s4ltf1sh.glance_widgets.ui.theme.Dimens
import com.s4ltf1sh.glance_widgets.ui.theme.MonthCalendarColors
import com.s4ltf1sh.glance_widgets.utils.CalendarWidgetUtils
import com.s4ltf1sh.glance_widgets.widget.component.GlanceIcon
import java.util.Calendar

@Composable
fun CalendarViewDefault(
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
    selectedDateBackground: ImageProvider
) {
    val context = LocalContext.current

    Column(modifier = modifier.background(monthCalendarColors.background)) {
        CalendarHeaderDefault(
            context = context,
            calendar = calendar,
            textColor = Color.White,
            textSize = 24.sp,
            iconSize = 14.dp,
            iconColor = Color.White,
            onGoToPreviousMonth = onGoToPreviousMonth,
            onGoToNextMonth = onGoToNextMonth
        )

        DaysOfWeek(
            textColor = Color.White,
            textSize = Dimens.smallFontSize,
            dayOfWeekNames = dayOfWeekNames
        )

        DatesDefault(
            calendar = calendar,
            dateTextSize = Dimens.smallFontSize,
            focusedDateColor = Color.White,
            unfocusedDateColor = Color.White.copy(alpha = 0.57F),
            onDateClick = onDateClick,
            selectedDateBackground = selectedDateBackground
        )
    }
}

@SuppressLint("RestrictedApi")
@Composable
fun CurrentDayWithLocationVertical(
    modifier: GlanceModifier = GlanceModifier,
    dayOfWeek: String,
    dayOfWeekColor: Color = Color.White,
    dayOfWeekSize: TextUnit,
    dayOfMonth: Int,
    dayOfMonthColor: Color = Color.White,
    dayOfMonthSize: TextUnit,
    location: String,
    locationColor: Color = Color.White,
    locationSize: TextUnit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = dayOfWeek,
            style = TextStyle(
                fontSize = dayOfWeekSize,
                textAlign = TextAlign.Center,
                color = ColorProvider(dayOfWeekColor),
                fontWeight = FontWeight.Medium
            ),
            modifier = GlanceModifier.wrapContentWidth()
        )

        Text(
            text = dayOfMonth.toString(),
            style = TextStyle(
                fontSize = dayOfMonthSize,
                textAlign = TextAlign.Center,
                color = ColorProvider(dayOfMonthColor),
                fontWeight = FontWeight.Bold
            ),
            modifier = GlanceModifier.wrapContentWidth()
        )

        Spacer(modifier = GlanceModifier.height(Dimens.padding10))

        Location(
            modifier = GlanceModifier,
            location = location,
            locationColor = locationColor,
            locationSize = locationSize
        )
    }
}

@SuppressLint("RestrictedApi")
@Composable
fun CurrentDayWithLocationHorizontal(
    modifier: GlanceModifier = GlanceModifier,
    dayOfWeek: String,
    dayOfWeekColor: Color = Color.White,
    dayOfWeekSize: TextUnit,
    dayOfMonth: Int,
    dayOfMonthColor: Color = Color.White,
    dayOfMonthSize: TextUnit,
    monthName: String,
    monthNameColor: Color = Color.White,
    monthNameSize: TextUnit,
    year: Int,
    yearColor: Color = Color.White,
    yearSize: TextUnit,
    location: String,
    locationColor: Color = Color.White,
    locationSize: TextUnit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = dayOfMonth.toString(),
            style = TextStyle(
                fontSize = dayOfMonthSize,
                textAlign = TextAlign.Start,
                color = ColorProvider(dayOfMonthColor)
            )
        )

        Spacer(modifier = GlanceModifier.width(Dimens.padding10))

        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = dayOfWeek,
                style = TextStyle(
                    fontSize = dayOfWeekSize,
                    textAlign = TextAlign.Start,
                    color = ColorProvider(dayOfWeekColor)
                )
            )

            Text(
                text = monthName,
                style = TextStyle(
                    fontSize = monthNameSize,
                    textAlign = TextAlign.Start,
                    color = ColorProvider(monthNameColor)
                ),
                modifier = GlanceModifier.defaultWeight()
            )
        }

        Spacer(modifier = GlanceModifier.defaultWeight())

        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = year.toString(),
                style = TextStyle(
                    fontSize = yearSize,
                    textAlign = TextAlign.End,
                    color = ColorProvider(yearColor)
                )
            )

            Spacer(
                modifier = GlanceModifier.height(16.dp)
            )

            Location(
                location = location,
                locationColor = locationColor,
                locationSize = locationSize
            )
        }

    }
}

@SuppressLint("RestrictedApi")
@Composable
private fun Location(
    modifier: GlanceModifier = GlanceModifier,
    location: String,
    locationColor: Color = Color.White,
    locationSize: TextUnit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.Start,
        modifier = modifier
    ) {
        Image(
            provider = ImageProvider(R.drawable.ic_location),
            contentDescription = null,
            modifier = GlanceModifier.size(12.dp)
        )

        Spacer(
            modifier = GlanceModifier.width(Dimens.size4)
        )

        Text(
            text = location,
            style = TextStyle(
                fontSize = locationSize,
                textAlign = TextAlign.Start,
                color = ColorProvider(locationColor)
            ),
            modifier = GlanceModifier.wrapContentWidth()
        )
    }
}

@SuppressLint("RestrictedApi")
@Composable
fun CalendarHeaderDefault(
    context: Context,
    modifier: GlanceModifier = GlanceModifier,
    calendar: Calendar,
    textSize: TextUnit,
    textColor: Color,
    iconSize: Dp,
    iconColor: Color,
    onGoToPreviousMonth: (() -> Unit)? = null,
    onGoToNextMonth: (() -> Unit)? = null,
    showActionButtons: Boolean = true
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (onGoToPreviousMonth != null && showActionButtons) {
            GlanceIcon(
                modifier = getHeaderImageModifier(
                    size = iconSize,
                    onClick = onGoToPreviousMonth
                ),
                resId = R.drawable.ic_widget_arrow_left,
                tint = iconColor,
                size = iconSize,
                onClick = onGoToPreviousMonth,
                contentDescription = getResString(context, R.string.desc_previous_month)
            )

            Spacer(modifier = GlanceModifier.defaultWeight())
        }

        Text(
            text = getFormatString(
                calendar.timeInMillis,
                CalendarWidgetUtils.MONTH_YEAR_FORMAT
            ),
            style = TextStyle(
                fontSize = textSize,
                textAlign = TextAlign.Center,
                color = ColorProvider(textColor),
                fontWeight = FontWeight.Medium
            )
        )

        if (onGoToNextMonth != null && showActionButtons) {
            Spacer(modifier = GlanceModifier.defaultWeight())

            GlanceIcon(
                modifier = getHeaderImageModifier(
                    size = iconSize,
                    onClick = onGoToNextMonth
                ),
                resId = R.drawable.ic_widget_arrow_right,
                tint = iconColor,
                size = iconSize,
                onClick = onGoToNextMonth,
                contentDescription = getResString(context, R.string.desc_next_month)
            )
        }
    }
}

private fun getResString(context: Context, stringResId: Int): String {
    return context.resources.getString(stringResId)
}

private fun getFormatString(timeInMillis: Long, requiredFormat: String): String {
    return CalendarWidgetUtils.formatDateTime(
        timeInMillis = timeInMillis,
        requiredFormat = requiredFormat
    )
}

@SuppressLint("RestrictedApi")
@Composable
fun DaysOfWeek(
    textColor: Color,
    textSize: TextUnit,
    dayOfWeekNames: List<String>
) {
    val safeWeekNames = if (dayOfWeekNames.size >= 7) {
        dayOfWeekNames.take(7)
    } else {
        // Fallback to default English names if invalid
        listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    }

    Row(modifier = GlanceModifier.fillMaxWidth()) {
        for (i in 0 until CalendarWidgetUtils.COLUMN_COUNT) {
            Text(
                modifier = GlanceModifier.defaultWeight(),
                text = safeWeekNames[i], // Index 0=Sunday, 1=Monday, ..., 6=Saturday
                style = TextStyle(
                    color = ColorProvider(textColor),
                    fontSize = textSize,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}

@Composable
fun DatesDefault(
    modifier: GlanceModifier = GlanceModifier,
    calendar: Calendar,
    dateTextSize: TextUnit,
    focusedDateColor: Color,
    unfocusedDateColor: Color = Color.White.copy(alpha = 0.6F),
    selectedDateColor: Color = focusedDateColor,
    showUnfocusedDates: Boolean = true,
    selectedDateBackground: ImageProvider,
    onDateClick: Action? = null
) {
    val todayCalendar = CalendarWidgetUtils.getCalendar()
    val dateCalendar = calendar.clone() as Calendar
    dateCalendar.set(Calendar.DAY_OF_MONTH, 1)
    CalendarWidgetUtils.setTimeToBeginningOfDay(dateCalendar)

    // Tính offset của ngày đầu tháng so với Sunday (0=Sunday, 1=Monday, ...)
    val firstDayOfMonthOffset = (dateCalendar.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY + 7) % 7

    // Lấy số ngày của tháng hiện tại
    val daysInCurrentMonth = dateCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    // Tính số ngày của tháng trước
    val prevMonthCalendar = dateCalendar.clone() as Calendar
    prevMonthCalendar.add(Calendar.MONTH, -1)
    val daysInPrevMonth = prevMonthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    // Tháng sau
    val nextMonthCalendar = dateCalendar.clone() as Calendar
    nextMonthCalendar.add(Calendar.MONTH, 1)

    // Tạo array để chứa tất cả các ngày cần hiển thị (42 ô = 6 tuần × 7 ngày)
    val totalCells = CalendarWidgetUtils.ROW_COUNT * CalendarWidgetUtils.COLUMN_COUNT
    val dateItems = Array<DateItem?>(totalCells) { null }

    // Fill các ngày của tháng trước (unfocused)
    if (showUnfocusedDates && firstDayOfMonthOffset > 0) {
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
    if (showUnfocusedDates && remainingCells > 0) {
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

    Column(modifier = modifier) {
        for (row in 0 until CalendarWidgetUtils.ROW_COUNT) {
            Row(
                modifier = GlanceModifier.fillMaxWidth().defaultWeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (column in 0 until CalendarWidgetUtils.COLUMN_COUNT) {
                    val position = row * CalendarWidgetUtils.COLUMN_COUNT + column
                    val dateItem = dateItems[position]
                    val isToday = dateItem?.let {
                        CalendarWidgetUtils.isSameDay(it.calendar, todayCalendar)
                    } == true

                    DateTextView(
                        dateString = dateItem?.dayString ?: "",
                        isToday = isToday,
                        onClick = onDateClick,
                        textSize = dateTextSize,
                        textColor = if (isToday)
                            selectedDateColor
                        else if (dateItem?.isCurrentMonth == true)
                            focusedDateColor
                        else
                            unfocusedDateColor,
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

@SuppressLint("RestrictedApi")
@Composable
fun RowScope.DateTextView(
    dateString: String,
    isToday: Boolean,
    onClick: Action?,
    textSize: TextUnit,
    textColor: Color,
    selectedDateBackground: ImageProvider
) {
    return DateTextView(
        modifier = getDateTextParentModifier(dateString.isNotEmpty(), onClick),
        dateString = dateString,
        isToday = isToday,
        onClick = onClick,
        textSize = textSize,
        textColor = textColor,
        selectedDateBackground = selectedDateBackground
    )
}

@SuppressLint("RestrictedApi")
@Composable
fun RowScope.DateTextView(
    modifier: GlanceModifier,
    dateString: String,
    isToday: Boolean,
    onClick: Action?,
    textSize: TextUnit,
    textColor: Color,
    selectedDateBackground: ImageProvider
) {
    val hasDate = dateString.isNotEmpty()

    Box(
        modifier = modifier.padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        // Background cho selected date
        if (isToday && hasDate) {
            Image(
                provider = selectedDateBackground,
                contentDescription = null,
                modifier = GlanceModifier.fillMaxSize()
            )
        }

        Text(
            modifier = getDateTextChildModifier(hasDate),
            text = dateString,
            style = TextStyle(
                color = ColorProvider(textColor),
                fontSize = textSize,
                textAlign = TextAlign.Center
            ),
            maxLines = 1
        )
    }
}

private fun RowScope.getDateTextParentModifier(
    isCurrentMonth: Boolean,
    onClick: Action?
): GlanceModifier {
    return if (isCurrentMonth && onClick != null) {
        GlanceModifier.defaultWeight().clickable(onClick = onClick)
    } else {
        GlanceModifier.defaultWeight()
    }
}

private fun getDateTextChildModifier(isCurrentMonth: Boolean): GlanceModifier {
    return if (isCurrentMonth) {
        GlanceModifier.wrapContentSize()
    } else {
        GlanceModifier.wrapContentSize()
    }
}

@Composable
private fun getHeaderImageModifier(
    size: Dp = Dimens.imageSize,
    onClick: () -> Unit
): GlanceModifier {
    return GlanceModifier.size(size)
        .clickable {
            onClick()
        }.padding(
            vertical = Dimens.defaultHalfPadding,
            horizontal = Dimens.defaultHalfPadding
        )
}