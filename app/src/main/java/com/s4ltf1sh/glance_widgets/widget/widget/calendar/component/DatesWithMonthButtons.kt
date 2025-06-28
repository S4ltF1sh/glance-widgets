package com.s4ltf1sh.glance_widgets.widget.widget.calendar.component

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.coerceAtMost
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.RowScope
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.s4ltf1sh.glance_widgets.utils.CalendarWidgetUtils
import com.s4ltf1sh.glance_widgets.utils.getMonthName
import java.util.Calendar

@Composable
fun DatesWithMonthButtons(
    modifier: GlanceModifier = GlanceModifier,
    calendar: Calendar,
    dateTextSize: TextUnit,
    dateTextColor: Color,
    selectedDateColor: Color = dateTextColor,
    selectedDateBackground: ImageProvider,
    monthButtonColor: Color = Color.White,
    monthButtonBackground: Color = Color.White.copy(0.2F),
    onDateClick: Action? = null,
    onNextMonthClick: () -> Unit = {},
    onPreviousMonthClick: () -> Unit = {}
) {
    val todayCalendar = CalendarWidgetUtils.getCalendar()
    val dateCalendar = calendar.clone() as Calendar
    dateCalendar.set(Calendar.DAY_OF_MONTH, 1)
    CalendarWidgetUtils.setTimeToBeginningOfDay(dateCalendar)

    // Tính offset của ngày đầu tháng so với Sunday (0=Sunday, 1=Monday, ...)
    val firstDayOfMonthOffset = (dateCalendar.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY + 7) % 7

    // Lấy số ngày của tháng hiện tại
    val daysInCurrentMonth = dateCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    // Tính toán tháng trước và tháng sau
    val prevMonthCalendar = dateCalendar.clone() as Calendar
    prevMonthCalendar.add(Calendar.MONTH, -1)
    val prevMonthName = prevMonthCalendar.getMonthName()

    val nextMonthCalendar = dateCalendar.clone() as Calendar
    nextMonthCalendar.add(Calendar.MONTH, 1)
    val nextMonthName = nextMonthCalendar.getMonthName()

    // Tính toán space available
    val lastDayPosition = firstDayOfMonthOffset + daysInCurrentMonth - 1
    val rowCount = (lastDayPosition / CalendarWidgetUtils.COLUMN_COUNT) + 1
    val totalCells = rowCount * CalendarWidgetUtils.COLUMN_COUNT
    val remainingCellsAfterLastDay = totalCells - 1 - lastDayPosition

    // Xác định hiển thị cho month buttons
    val prevMonthInfo = getMonthButtonDisplayInfo(prevMonthName, firstDayOfMonthOffset)
    val nextMonthInfo = getMonthButtonDisplayInfo(nextMonthName, remainingCellsAfterLastDay)

    val width = LocalSize.current.width
    val columnCount = CalendarWidgetUtils.COLUMN_COUNT
    val cellWidth = (width / columnCount).coerceAtMost(width / (columnCount + 1))
    val prevMonthButtonWidth = cellWidth * prevMonthInfo.availableSpace
    val nextMonthButtonWidth = cellWidth * nextMonthInfo.availableSpace

    Column(
        modifier = modifier.width(width).fillMaxHeight()
    ) {
        for (row in 0 until rowCount) {
            Row(
                modifier = GlanceModifier.width(width).defaultWeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (
                    prevMonthInfo.shouldShow &&
                    row == 0
                ) {
                    MonthButtonCell(
                        modifier = GlanceModifier.width(prevMonthButtonWidth),
                        text = prevMonthInfo.displayText,
                        onClick = onPreviousMonthClick,
                        textColor = monthButtonColor,
                        backgroundColor = monthButtonBackground,
                        textSize = dateTextSize
                    )
                }

                val startColumn = if (row == 0) firstDayOfMonthOffset else 0

                for (column in startColumn until CalendarWidgetUtils.COLUMN_COUNT) {
                    val position = row * CalendarWidgetUtils.COLUMN_COUNT + column

                    if (position > lastDayPosition) {
                        break
                    }

                    val dayNumber = position - firstDayOfMonthOffset + 1
                    val dayCalendar = dateCalendar.clone() as Calendar
                    dayCalendar.set(Calendar.DAY_OF_MONTH, dayNumber)
                    val isToday = CalendarWidgetUtils.isSameDay(dayCalendar, todayCalendar)

                    DateTextView(
                        modifier = GlanceModifier.width(cellWidth),
                        dateString = dayNumber.toString(),
                        isToday = isToday,
                        onClick = onDateClick,
                        textSize = dateTextSize,
                        textColor = if (isToday) selectedDateColor else dateTextColor,
                        selectedDateBackground = selectedDateBackground
                    )
                }

                if (
                    nextMonthInfo.shouldShow &&
                    row == rowCount - 1
                ) {
                    MonthButtonCell(
                        modifier = GlanceModifier.width(nextMonthButtonWidth),
                        text = nextMonthInfo.displayText,
                        onClick = onNextMonthClick,
                        textColor = monthButtonColor,
                        backgroundColor = monthButtonBackground,
                        textSize = dateTextSize
                    )
                }
            }
        }
    }
}

private data class MonthButtonDisplayInfo(
    val displayText: String,
    val shouldShow: Boolean,
    val availableSpace: Int
)

private fun getMonthButtonDisplayInfo(
    monthName: String,
    availableSpace: Int
): MonthButtonDisplayInfo {
    return when (availableSpace) {
        0 -> MonthButtonDisplayInfo(
            displayText = "",
            shouldShow = false,
            availableSpace = 0
        )

        1 -> MonthButtonDisplayInfo(
            displayText = monthName.take(3), // Feb, Mar, etc.
            shouldShow = true,
            availableSpace = 1
        )

        else -> MonthButtonDisplayInfo(
            displayText = monthName,
            shouldShow = true,
            availableSpace = availableSpace
        )
    }
}

@SuppressLint("RestrictedApi")
@Composable
private fun RowScope.MonthButtonCell(
    modifier: GlanceModifier,
    text: String,
    onClick: (() -> Unit)? = null,
    textColor: Color,
    backgroundColor: Color,
    textSize: TextUnit
) {
    Box(
        modifier = modifier.padding(horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = TextStyle(
                color = ColorProvider(textColor),
                fontSize = textSize,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            ),
            maxLines = 1,
            modifier = GlanceModifier.fillMaxWidth()
                .padding(vertical = 6.dp)
                .cornerRadius(50.dp)
                .background(backgroundColor)
                .then(
                    if (onClick != null) {
                        GlanceModifier.clickable {
                            onClick.invoke()
                        }
                    } else {
                        GlanceModifier
                    }
                )
        )
    }
}
