package com.s4ltf1sh.glance_widgets.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

internal object MonthCalendarWidgetUtils {

    const val ROW_COUNT = 6
    const val COLUMN_COUNT = 7

    const val DAY_FORMAT = "EEE"
    const val MONTH_DATE_FORMAT = "MMM dd"
    const val MONTH_YEAR_FORMAT = "MMM yyyy"
    const val DATE_FORMAT = "dd"
    const val WEEK_DAY_FORMAT = "EEEEE"

    fun getCalendar(timeInMillis: Long? = null): Calendar {
        val calendar = Calendar.getInstance()
        timeInMillis?.let {
            calendar.timeInMillis = it
        }

        return calendar
    }

    fun getFirstDayOfWeek(calendar: Calendar): Int {
        return calendar.get(Calendar.DAY_OF_WEEK) - 1 // Convert to 0-based (0=Sunday, 6=Saturday)
    }

    fun getFirstDayOfMonth(calendar: Calendar): Int {
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        return calendar.get(Calendar.DAY_OF_WEEK) - 1 // Convert to 0-based (0=Sunday, 6=Saturday)
    }

    fun setTimeToBeginningOfDay(calendar: Calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
    }

    fun isSameDay(dayOne: Calendar, dayTwo: Calendar): Boolean {
        return dayOne.get(Calendar.YEAR) == dayTwo.get(Calendar.YEAR)
                && dayOne.get(Calendar.DAY_OF_YEAR) == dayTwo.get(Calendar.DAY_OF_YEAR)
    }

    fun formatDateTime(
        timeInMillis: Long,
        requiredFormat: String): String {
        val requiredSimpleDateFormat = SimpleDateFormat(requiredFormat,  Locale.getDefault())
        return requiredSimpleDateFormat.format(Date(timeInMillis))
    }

}