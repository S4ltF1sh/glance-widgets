package com.s4ltf1sh.glance_widgets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s4ltf1sh.glance_widgets.db.WidgetModelRepository
import com.s4ltf1sh.glance_widgets.db.clock.ClockDigitalEntity
import com.s4ltf1sh.glance_widgets.db.quote.QuoteEntity
import com.s4ltf1sh.glance_widgets.model.WidgetSize
import com.s4ltf1sh.glance_widgets.model.WidgetType
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
    private val widgetRepository: WidgetModelRepository
) : ViewModel() {
    private val _quotes: MutableStateFlow<List<QuoteEntity>> = MutableStateFlow(emptyList())
    val quotes = _quotes.asStateFlow()

    private val _clockDigitals = MutableStateFlow<List<ClockDigitalEntity>>(emptyList())
    val clockDigitals: StateFlow<List<ClockDigitalEntity>> = _clockDigitals.asStateFlow()


    fun getQuotesBySize(size: WidgetSize) = viewModelScope.launch {
        widgetRepository.getQuotesBySize(size).distinctUntilChanged()
            .collectLatest { comments ->
                _quotes.update { comments }
            }
    }

    fun insertQuotes(quotes: List<QuoteEntity>) = viewModelScope.launch {
        widgetRepository.insertQuotes(quotes)
    }

    // Clock Digital operations
    fun getClockDigitalsBySize(size: WidgetSize) {
        viewModelScope.launch {
            widgetRepository.getClockDigitalsBySize(size).collect { clockDigitalList ->
                _clockDigitals.value = clockDigitalList
            }
        }
    }

    fun insertClockDigitals(clockDigitals: List<ClockDigitalEntity>) {
        viewModelScope.launch {
            widgetRepository.insertClockDigitals(clockDigitals)
        }
    }
}