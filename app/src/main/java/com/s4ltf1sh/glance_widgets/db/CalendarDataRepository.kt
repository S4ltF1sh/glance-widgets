package com.s4ltf1sh.glance_widgets.db

import android.content.Context
import com.s4ltf1sh.glance_widgets.db.calendar.WidgetCalendarData
import com.s4ltf1sh.glance_widgets.model.WidgetSize
import com.s4ltf1sh.glance_widgets.model.WidgetType
import com.s4ltf1sh.glance_widgets.utils.CalendarUtils
import com.s4ltf1sh.glance_widgets.widget.widget.calendar.CalendarWidgetWorker
import com.squareup.moshi.Moshi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalendarDataRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val moshi: Moshi
) {
    
    /**
     * Create default calendar data for current month
     */
    fun createDefaultCalendarData(): WidgetCalendarData {
        val (year, month, day) = CalendarUtils.getCurrentDateInfo()
        return WidgetCalendarData(
            year = year,
            month = month,
            selectedDay = null,
            todayDay = day,
            backgroundPath = null,
        )
    }
    
    /**
     * Create calendar data for specific month
     */
    fun createCalendarData(
        year: Int,
        month: Int,
        selectedDay: Int? = null,
        backgroundImagePath: String? = null
    ): WidgetCalendarData {
        val currentDate = CalendarUtils.getCurrentDateInfo()
        val todayDay = if (year == currentDate.first && month == currentDate.second) {
            currentDate.third
        } else null
        
        return WidgetCalendarData(
            year = year,
            month = month,
            selectedDay = selectedDay,
            todayDay = todayDay,
            backgroundPath = backgroundImagePath,
        )
    }
    
    /**
     * Navigate to next month
     */
    fun navigateToNextMonth(currentData: WidgetCalendarData): WidgetCalendarData {
        val (nextYear, nextMonth) = CalendarUtils.getNextMonth(currentData.year, currentData.month)
        return createCalendarData(
            year = nextYear,
            month = nextMonth,
            selectedDay = null, // Clear selection when navigating
            backgroundImagePath = currentData.backgroundPath
        )
    }
    
    /**
     * Navigate to previous month
     */
    fun navigateToPreviousMonth(currentData: WidgetCalendarData): WidgetCalendarData {
        val (prevYear, prevMonth) = CalendarUtils.getPreviousMonth(currentData.year, currentData.month)
        return createCalendarData(
            year = prevYear,
            month = prevMonth,
            selectedDay = null, // Clear selection when navigating
            backgroundImagePath = currentData.backgroundPath
        )
    }
    
    /**
     * Select a specific day
     */
    fun selectDay(currentData: WidgetCalendarData, day: Int): WidgetCalendarData {
        return currentData.copy(selectedDay = day)
    }
    
    /**
     * Update background image
     */
    fun updateBackgroundImage(currentData: WidgetCalendarData, imagePath: String?): WidgetCalendarData {
        return currentData.copy(backgroundPath = imagePath)
    }
    
    /**
     * Reset to current month
     */
    fun resetToCurrentMonth(currentData: WidgetCalendarData): WidgetCalendarData {
        val (year, month, day) = CalendarUtils.getCurrentDateInfo()
        return currentData.copy(
            year = year,
            month = month,
            selectedDay = null,
            todayDay = day
        )
    }
    
    /**
     * Parse calendar data from JSON string
     */
    fun parseCalendarData(jsonData: String): WidgetCalendarData? {
        return try {
            moshi.adapter(WidgetCalendarData::class.java).fromJson(jsonData)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Convert calendar data to JSON string
     */
    fun calendarDataToJson(calendarData: WidgetCalendarData): String {
        return moshi.adapter(WidgetCalendarData::class.java).toJson(calendarData)
    }
    
    /**
     * Update widget with new calendar data
     */
    suspend fun updateWidget(
        widgetId: Int,
        calendarData: WidgetCalendarData
    ) = withContext(Dispatchers.IO) {
        CalendarWidgetWorker.enqueue(
            context = context,
            widgetId = widgetId,
            type = WidgetType.Calendar.Type1,
            widgetSize = WidgetSize.MEDIUM,
            year = calendarData.year,
            month = calendarData.month,
            selectedDay = calendarData.selectedDay,
            backgroundImageUrl = null // Background image should already be downloaded
        )
    }
    
    /**
     * Get formatted date string
     */
    fun getFormattedDate(calendarData: WidgetCalendarData, day: Int): String {
        return CalendarUtils.formatDate(calendarData.year, calendarData.month, day)
    }
    
    /**
     * Get month display name
     */
    fun getMonthDisplayName(calendarData: WidgetCalendarData, isShort: Boolean = false): String {
        return CalendarUtils.getMonthName(calendarData.month, isShort)
    }
    
    /**
     * Get calendar weeks for display
     */
    fun getCalendarWeeks(calendarData: WidgetCalendarData): List<List<Int?>> {
        return CalendarUtils.getCalendarWeeks(calendarData.year, calendarData.month)
    }
    
    /**
     * Check if a specific day is today
     */
    fun isDayToday(calendarData: WidgetCalendarData, day: Int): Boolean {
        return calendarData.todayDay == day
    }
    
    /**
     * Check if a specific day is selected
     */
    fun isDaySelected(calendarData: WidgetCalendarData, day: Int): Boolean {
        return calendarData.selectedDay == day
    }

    /**
     * Validate calendar data
     */
    fun isValidCalendarData(calendarData: WidgetCalendarData): Boolean {
        return try {
            // Check if year is reasonable
            if (calendarData.year < 1900 || calendarData.year > 2200) return false
            
            // Check if month is valid
            if (calendarData.month < 1 || calendarData.month > 12) return false
            
            // Check if selected day is valid for the month
            val daysInMonth = CalendarUtils.getDaysInMonth(calendarData.year, calendarData.month)
            if (calendarData.selectedDay != null && 
                (calendarData.selectedDay < 1 || calendarData.selectedDay > daysInMonth)) {
                return false
            }
            
            // Check if today day is valid for the month
            if (calendarData.todayDay != null && 
                (calendarData.todayDay < 1 || calendarData.todayDay > daysInMonth)) {
                return false
            }
            
            true
        } catch (e: Exception) {
            false
        }
    }
}