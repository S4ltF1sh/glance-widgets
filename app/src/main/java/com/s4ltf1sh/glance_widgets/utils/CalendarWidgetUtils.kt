package com.s4ltf1sh.glance_widgets.utils

import androidx.glance.ImageProvider
import com.s4ltf1sh.glance_widgets.R
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetType
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

internal object CalendarWidgetUtils {

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
        requiredFormat: String
    ): String {
        val requiredSimpleDateFormat = SimpleDateFormat(requiredFormat, Locale.getDefault())
        return requiredSimpleDateFormat.format(Date(timeInMillis))
    }


    fun getTodayDayOfMonth(): Int {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    }

    fun getTodayDayOfWeek(fullName: Boolean = true): String {
        return Calendar.getInstance().getDayOfWeekName(fullName = fullName)
    }

    fun getCurrentMonthName(
        locale: Locale = Locale.getDefault(),
        fullName: Boolean = true
    ): String {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH)
        calendar.set(Calendar.MONTH, month + 1) // Convert to 1-based (1=January, 12=December)

        val format = if (fullName) {
            SimpleDateFormat("MMMM", locale)
        } else {
            SimpleDateFormat("MMM", locale)
        }

        return format.format(calendar.time)
    }

    fun getCurrentYear(): Int {
        return Calendar.getInstance().get(Calendar.YEAR)
    }

    fun getCurrentMonthAndYear(
        locale: Locale = Locale.getDefault()
    ): String {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH)
        calendar.set(Calendar.MONTH, month + 1) // Convert to 1-based (1=January, 12=December)
        val format = SimpleDateFormat(MONTH_YEAR_FORMAT, locale)
        return format.format(calendar.time)
    }
}

fun Calendar.getDayOfWeekName(
    locale: Locale = Locale.getDefault(),
    fullName: Boolean = true
): String {
    val dayOfWeek = get(Calendar.DAY_OF_WEEK)
    set(Calendar.DAY_OF_WEEK, dayOfWeek + 1) // Convert to 1-based (1=Sunday, 7=Saturday)

    val format = if (fullName) {
        SimpleDateFormat("EEEE", locale)
    } else {
        SimpleDateFormat("EEE", locale)
    }

    return format.format(time)
}

fun Calendar.getMonthName(
    locale: Locale = Locale.getDefault(),
    fullName: Boolean = true
): String {
    val month = get(Calendar.MONTH)
    set(Calendar.MONTH, month + 1) // Convert to 1-based (1=January, 12=December)

    val format = if (fullName) {
        SimpleDateFormat("MMMM", locale)
    } else {
        SimpleDateFormat("MMM", locale)
    }

    return format.format(time)
}

fun selectedDateBackground(glanceWidgetType: GlanceWidgetType.Calendar): ImageProvider {
    return ImageProvider(
        when (glanceWidgetType) {
            is GlanceWidgetType.Calendar.Type1Glance -> R.drawable.calendar_selected_bg_1
            is GlanceWidgetType.Calendar.Type2Glance -> R.drawable.calendar_selected_bg_2
            is GlanceWidgetType.Calendar.Type3Glance -> R.drawable.calendar_selected_bg_3
            is GlanceWidgetType.Calendar.Type5Glance -> R.drawable.calendar_selected_bg_5
            else -> R.drawable.calendar_selected_bg_3 // Default background
        }
    )
}