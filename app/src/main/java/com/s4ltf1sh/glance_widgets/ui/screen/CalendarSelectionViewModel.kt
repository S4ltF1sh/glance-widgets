package com.s4ltf1sh.glance_widgets.ui.screen

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s4ltf1sh.glance_widgets.db.WidgetModelRepository
import com.s4ltf1sh.glance_widgets.db.calendar.CalendarEntity
import com.s4ltf1sh.glance_widgets.db.calendar.WidgetCalendarData
import com.s4ltf1sh.glance_widgets.model.WidgetSize
import com.s4ltf1sh.glance_widgets.model.WidgetType
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
    val calendars: List<CalendarEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface CalendarSelectionEvent {
    data class CalendarConfigured(val calendar: CalendarEntity) : CalendarSelectionEvent
}

@HiltViewModel
class CalendarSelectionViewModel @Inject constructor(
    private val widgetRepository: WidgetModelRepository,
    private val eventChannel: EventChannel<CalendarSelectionEvent>
) : ViewModel(), HasEventFlow<CalendarSelectionEvent> by eventChannel {

    private val _uiState = MutableStateFlow(CalendarSelectionUiState())
    val uiState: StateFlow<CalendarSelectionUiState> = _uiState.asStateFlow()

    companion object {
        private const val TAG = "CalendarSelectionVM"
    }

    fun loadCalendars(size: WidgetSize) = viewModelScope.launch {
        widgetRepository.getCalendarsBySize(size).collect {
            _uiState.value = _uiState.value.copy(calendars = it, isLoading = false, error = null)
            Log.d(TAG, "Loaded calendars: ${it.size} items for size $size")
        }
    }

    fun confirmCalendarConfiguration(
        context: Context,
        widgetId: Int,
        calendarType: WidgetType.Calendar,
        widgetSize: WidgetSize,
        backgroundImageUrl: String?
    ) = viewModelScope.launch {
        try {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val calendarData = _uiState.value.calendars

            Log.d(
                TAG,
                "Configuring calendar widget - ID: $widgetId, Type: $calendarType, Size: $widgetSize"
            )
            Log.d(TAG, "Calendar data: $calendarData")

            // Enqueue the worker to setup the calendar widget


            _uiState.value = _uiState.value.copy(isLoading = false)

            Log.d(TAG, "Calendar widget configuration completed successfully")

        } catch (e: Exception) {
            Log.e(TAG, "Error configuring calendar widget", e)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = "Failed to configure calendar: ${e.message}"
            )
        }
    }
}