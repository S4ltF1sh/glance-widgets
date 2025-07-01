package com.s4ltf1sh.glance_widgets.ui.screen.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s4ltf1sh.glance_widgets.db.GlanceWidgetRepository
import com.s4ltf1sh.glance_widgets.db.WeatherRepository
import com.s4ltf1sh.glance_widgets.db.weather.GlanceWeatherEntity
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetSize
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WeatherSelectionUiState(
    val weatherList: List<GlanceWeatherEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class WeatherSelectionViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository, // Assuming a repository to fetch weather data
    private val widgetRepository: GlanceWidgetRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherSelectionUiState())
    val uiState = _uiState.asStateFlow()

    fun loadWeathers(size: GlanceWidgetSize) = viewModelScope.launch {
        // This function will load weather data based on the widget size
        // It should interact with the WeatherRepository to fetch the data
        // and update the _uiState accordingly.
        // For example:
        _uiState.update { currentState ->
            currentState.copy(isLoading = true, error = null)
        }

        widgetRepository.getWeatherBySize(size).collect { weatherList ->
            _uiState.update { currentState ->
                currentState.copy(
                    weatherList = weatherList,
                    isLoading = false,
                    error = null
                )
            }
        }
    }

    // Additional methods for handling user interactions, updating weather data, etc.
}