package com.s4ltf1sh.glance_widgets.ui.screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s4ltf1sh.glance_widgets.db.GlanceWidgetRepository
import com.s4ltf1sh.glance_widgets.db.calendar.GlanceCalendarEntity
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetSize
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CalendarSelectionUiState(
    val calendars: List<GlanceCalendarEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CalendarSelectionViewModel @Inject constructor(
    private val widgetRepository: GlanceWidgetRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarSelectionUiState())
    val uiState: StateFlow<CalendarSelectionUiState> = _uiState.asStateFlow()

    companion object {
        private const val TAG = "CalendarSelectionVM"
    }

    fun loadCalendars(size: GlanceWidgetSize) = viewModelScope.launch {
        widgetRepository.getCalendarsBySize(size).collect {
            _uiState.value = _uiState.value.copy(calendars = it, isLoading = false, error = null)
            Log.d(TAG, "Loaded calendars: ${it.size} items for size $size")
        }
    }
}