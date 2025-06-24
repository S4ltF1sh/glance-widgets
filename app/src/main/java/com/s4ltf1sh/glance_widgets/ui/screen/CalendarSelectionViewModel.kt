package com.s4ltf1sh.glance_widgets.ui.screen

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s4ltf1sh.glance_widgets.db.calendar.WidgetCalendarData
import com.s4ltf1sh.glance_widgets.model.WidgetSize
import com.s4ltf1sh.glance_widgets.model.WidgetType
import com.s4ltf1sh.glance_widgets.utils.CalendarUtils
import com.s4ltf1sh.glance_widgets.utils.EventChannel
import com.s4ltf1sh.glance_widgets.utils.HasEventFlow
import com.s4ltf1sh.glance_widgets.widget.widget.calendar.CalendarWidgetWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CalendarSelectionUiState(
    val calendarData: WidgetCalendarData = createDefaultCalendarData(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface CalendarSelectionEvent {
    data class CalendarConfigured(val calendarData: WidgetCalendarData) : CalendarSelectionEvent
}

@HiltViewModel
class CalendarSelectionViewModel @Inject constructor(
    private val eventChannel: EventChannel<CalendarSelectionEvent>
) : ViewModel(), HasEventFlow<CalendarSelectionEvent> by eventChannel {
    
    private val _uiState = MutableStateFlow(CalendarSelectionUiState())
    val uiState: StateFlow<CalendarSelectionUiState> = _uiState.asStateFlow()
    
    companion object {
        private const val TAG = "CalendarSelectionVM"
    }
    
    fun setMonth(month: Int) {
        val currentData = _uiState.value.calendarData
        val newData = updateCalendarDataWithNewDate(
            currentData = currentData,
            newYear = currentData.year,
            newMonth = month
        )
        _uiState.value = _uiState.value.copy(calendarData = newData)
        Log.d(TAG, "Month changed to: $month")
    }
    
    fun setYear(year: Int) {
        val currentData = _uiState.value.calendarData
        val newData = updateCalendarDataWithNewDate(
            currentData = currentData,
            newYear = year,
            newMonth = currentData.month
        )
        _uiState.value = _uiState.value.copy(calendarData = newData)
        Log.d(TAG, "Year changed to: $year")
    }
    
    fun selectDay(day: Int?) {
        val currentData = _uiState.value.calendarData
        val newData = currentData.copy(selectedDay = day)
        _uiState.value = _uiState.value.copy(calendarData = newData)
        Log.d(TAG, "Day selected: $day")
    }
    
    fun setBackgroundImage(imagePath: String?) {
        val currentData = _uiState.value.calendarData
        val newData = currentData.copy(backgroundPath = imagePath)
        _uiState.value = _uiState.value.copy(calendarData = newData)
        Log.d(TAG, "Background image set: $imagePath")
    }
    
    fun confirmCalendarConfiguration(
        context: Context,
        widgetId: Int,
        calendarType: WidgetType.Calendar,
        widgetSize: WidgetSize
    ) = viewModelScope.launch {
        try {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            val calendarData = _uiState.value.calendarData
            
            Log.d(TAG, "Configuring calendar widget - ID: $widgetId, Type: $calendarType, Size: $widgetSize")
            Log.d(TAG, "Calendar data: $calendarData")
            
            // Enqueue the worker to setup the calendar widget
            CalendarWidgetWorker.enqueue(
                context = context,
                widgetId = widgetId,
                type = calendarType,
                widgetSize = widgetSize,
                year = calendarData.year,
                month = calendarData.month,
                selectedDay = calendarData.selectedDay,
                backgroundImageUrl = null // Background image is already local
            )
            
            _uiState.value = _uiState.value.copy(isLoading = false)
            
            // Send event to close the screen
            eventChannel.send(CalendarSelectionEvent.CalendarConfigured(calendarData))
            
            Log.d(TAG, "Calendar widget configuration completed successfully")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error configuring calendar widget", e)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = "Failed to configure calendar: ${e.message}"
            )
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    /**
     * Navigate to the next month
     */
    fun navigateToNextMonth() {
        val currentData = _uiState.value.calendarData
        val (nextYear, nextMonth) = CalendarUtils.getNextMonth(currentData.year, currentData.month)
        
        val newData = updateCalendarDataWithNewDate(
            currentData = currentData,
            newYear = nextYear,
            newMonth = nextMonth
        )
        
        _uiState.value = _uiState.value.copy(calendarData = newData)
        Log.d(TAG, "Navigated to next month: $nextMonth/$nextYear")
    }
    
    /**
     * Navigate to the previous month
     */
    fun navigateToPreviousMonth() {
        val currentData = _uiState.value.calendarData
        val (prevYear, prevMonth) = CalendarUtils.getPreviousMonth(currentData.year, currentData.month)
        
        val newData = updateCalendarDataWithNewDate(
            currentData = currentData,
            newYear = prevYear,
            newMonth = prevMonth
        )
        
        _uiState.value = _uiState.value.copy(calendarData = newData)
        Log.d(TAG, "Navigated to previous month: $prevMonth/$prevYear")
    }
    
    /**
     * Reset to current month
     */
    fun resetToCurrentMonth() {
        val newData = createDefaultCalendarData()
        _uiState.value = _uiState.value.copy(calendarData = newData)
        Log.d(TAG, "Reset to current month")
    }
    
    /**
     * Update calendar data with new date while preserving today highlight
     */
    private fun updateCalendarDataWithNewDate(
        currentData: WidgetCalendarData,
        newYear: Int,
        newMonth: Int
    ): WidgetCalendarData {
        val currentDate = CalendarUtils.getCurrentDateInfo()
        val todayDay = if (newYear == currentDate.first && newMonth == currentDate.second) {
            currentDate.third
        } else null
        
        return currentData.copy(
            year = newYear,
            month = newMonth,
            todayDay = todayDay,
            selectedDay = null // Clear selection when changing month/year
        )
    }
}

/**
 * Create default calendar data for current month
 */
private fun createDefaultCalendarData(): WidgetCalendarData {
    val (year, month, day) = CalendarUtils.getCurrentDateInfo()
    return WidgetCalendarData(
        year = year,
        month = month,
        selectedDay = null,
        todayDay = day,
        backgroundPath = null,
    )
}