package com.s4ltf1sh.glance_widgets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s4ltf1sh.glance_widgets.db.GlanceWidgetRepository
import com.s4ltf1sh.glance_widgets.db.calendar.GlanceCalendarEntity
import com.s4ltf1sh.glance_widgets.db.clock.GlanceClockAnalogEntity
import com.s4ltf1sh.glance_widgets.db.clock.GlanceClockDigitalEntity
import com.s4ltf1sh.glance_widgets.db.quote.GlanceQuoteEntity
import com.s4ltf1sh.glance_widgets.db.weather.GlanceWeatherEntity
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetSize
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val widgetRepository: GlanceWidgetRepository
) : ViewModel() {
    private val _quotes: MutableStateFlow<List<GlanceQuoteEntity>> = MutableStateFlow(emptyList())
    val quotes = _quotes.asStateFlow()

    private val _clockDigitals = MutableStateFlow<List<GlanceClockDigitalEntity>>(emptyList())
    val clockDigitals: StateFlow<List<GlanceClockDigitalEntity>> = _clockDigitals.asStateFlow()

    private val _clockAnalogs = MutableStateFlow<List<GlanceClockAnalogEntity>>(emptyList())
    val clockAnalogs: StateFlow<List<GlanceClockAnalogEntity>> = _clockAnalogs.asStateFlow()


    fun getQuotesBySize(size: GlanceWidgetSize) = viewModelScope.launch {
        widgetRepository.getQuotesBySize(size).distinctUntilChanged()
            .collectLatest { comments ->
                _quotes.update { comments }
            }
    }

    fun insertQuotes(quotes: List<GlanceQuoteEntity>) = viewModelScope.launch {
        widgetRepository.insertQuotes(quotes)
    }

    // Clock Digital operations
    fun getClockDigitalsBySize(size: GlanceWidgetSize) {
        viewModelScope.launch {
            widgetRepository.getClockDigitalsBySize(size).collect { clockDigitalList ->
                _clockDigitals.value = clockDigitalList
            }
        }
    }

    fun insertClockDigitals(clockDigitals: List<GlanceClockDigitalEntity>) {
        viewModelScope.launch {
            widgetRepository.insertClockDigitals(clockDigitals)
        }
    }

    // Clock Analog operations
    fun getClockAnalogsBySize(size: GlanceWidgetSize) = viewModelScope.launch {
        widgetRepository.getClockAnalogBySize(size).distinctUntilChanged()
            .collectLatest { clockAnalogList ->
                _clockAnalogs.update { clockAnalogList }
            }
    }

    fun insertClockAnalogs(clockAnalogs: List<GlanceClockAnalogEntity>) = viewModelScope.launch {
        widgetRepository.insertClockAnalogs(clockAnalogs)
    }

    // Calendar operations
    fun insertCalendars(calendars: List<GlanceCalendarEntity>) = viewModelScope.launch {
        widgetRepository.insertCalendars(calendars)
    }

    // Weather operations
    fun insertWeathers(weathers: List<GlanceWeatherEntity>) = viewModelScope.launch {
        widgetRepository.insertWeathers(weathers)
    }
}