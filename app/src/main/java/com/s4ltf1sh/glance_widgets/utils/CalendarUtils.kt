package com.s4ltf1sh.glance_widgets.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object CalendarUtils {

    /**
     * Get the current date information
     */
    fun getCurrentDateInfo(): Triple<Int, Int, Int> {
        val calendar = Calendar.getInstance()
        return Triple(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1, // Calendar.MONTH is 0-based
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    /**
     * Get the number of days in a specific month
     */
    fun getDaysInMonth(year: Int, month: Int): Int {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month - 1) // Calendar.MONTH is 0-based
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    /**
     * Get the first day of week for a specific month (0 = Sunday, 6 = Saturday)
     */
    fun getFirstDayOfWeek(year: Int, month: Int): Int {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month - 1) // Calendar.MONTH is 0-based
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        return dayOfWeek - 1 // Convert to 0-based (0=Sunday, 6=Saturday)
    }

    /**
     * Check if a date is today
     */
    fun isToday(year: Int, month: Int, day: Int): Boolean {
        val today = Calendar.getInstance()
        return year == today.get(Calendar.YEAR) &&
                month == (today.get(Calendar.MONTH) + 1) &&
                day == today.get(Calendar.DAY_OF_MONTH)
    }

    /**
     * Format month name
     */
    fun getMonthName(month: Int, isShort: Boolean = false): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, month - 1) // Calendar.MONTH is 0-based

        val format = if (isShort) "MMM" else "MMMM"
        val dateFormat = SimpleDateFormat(format, Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    /**
     * Get day of week name
     */
    fun getDayOfWeekName(year: Int, month: Int, day: Int, isShort: Boolean = true): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month - 1) // Calendar.MONTH is 0-based
        calendar.set(Calendar.DAY_OF_MONTH, day)

        val format = if (isShort) "EEE" else "EEEE"
        val dateFormat = SimpleDateFormat(format, Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    /**
     * Navigate to next month
     */
    fun getNextMonth(year: Int, month: Int): Pair<Int, Int> {
        return if (month == 12) {
            Pair(year + 1, 1)
        } else {
            Pair(year, month + 1)
        }
    }

    /**
     * Navigate to previous month
     */
    fun getPreviousMonth(year: Int, month: Int): Pair<Int, Int> {
        return if (month == 1) {
            Pair(year - 1, 12)
        } else {
            Pair(year, month - 1)
        }
    }

    /**
     * Get calendar weeks for a month
     */
    fun getCalendarWeeks(year: Int, month: Int): List<List<Int?>> {
        val firstDayOfWeek = getFirstDayOfWeek(year, month)
        val daysInMonth = getDaysInMonth(year, month)

        val weeks = mutableListOf<List<Int?>>()
        var currentWeek = mutableListOf<Int?>()

        // Add empty cells for days before first day of month
        repeat(firstDayOfWeek) {
            currentWeek.add(null)
        }

        // Add days of the month
        for (day in 1..daysInMonth) {
            currentWeek.add(day)

            // If week is complete, add it to weeks list
            if (currentWeek.size == 7) {
                weeks.add(currentWeek.toList())
                currentWeek.clear()
            }
        }

        // Add empty cells for remaining days in last week
        if (currentWeek.isNotEmpty()) {
            while (currentWeek.size < 7) {
                currentWeek.add(null)
            }
            weeks.add(currentWeek.toList())
        }

        return weeks
    }

    /**
     * Format date as string
     */
    fun formatDate(year: Int, month: Int, day: Int, pattern: String = "dd/MM/yyyy"): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month - 1) // Calendar.MONTH is 0-based
        calendar.set(Calendar.DAY_OF_MONTH, day)

        val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    /**
     * Get week number in year
     */
    fun getWeekOfYear(year: Int, month: Int, day: Int): Int {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month - 1) // Calendar.MONTH is 0-based
        calendar.set(Calendar.DAY_OF_MONTH, day)

        return calendar.get(Calendar.WEEK_OF_YEAR)
    }

    /**
     * Check if year is leap year
     */
    fun isLeapYear(year: Int): Boolean {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, Calendar.FEBRUARY)
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH) == 29
    }

    /**
     * Get day headers for calendar
     */
    fun getDayHeaders(isShort: Boolean = true): List<String> {
        val calendar = Calendar.getInstance()
        val headers = mutableListOf<String>()

        // Start with Sunday (Calendar.SUNDAY = 1)
        for (i in Calendar.SUNDAY..Calendar.SATURDAY) {
            calendar.set(Calendar.DAY_OF_WEEK, i)
            val format = if (isShort) "E" else "EEEE"
            val dateFormat = SimpleDateFormat(format, Locale.getDefault())
            headers.add(dateFormat.format(calendar.time))
        }

        return headers
    }

    /**
     * Check if two dates are the same
     */
    fun isSameDate(year1: Int, month1: Int, day1: Int, year2: Int, month2: Int, day2: Int): Boolean {
        return year1 == year2 && month1 == month2 && day1 == day2
    }

    /**
     * Get current timestamp
     */
    fun getCurrentTimestamp(): Long {
        return System.currentTimeMillis()
    }

    /**
     * Convert timestamp to date info
     */
    fun timestampToDateInfo(timestamp: Long): Triple<Int, Int, Int> {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        return Triple(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1, // Calendar.MONTH is 0-based
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }
}