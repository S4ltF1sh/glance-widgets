package com.s4ltf1sh.glance_widgets.di

import com.s4ltf1sh.glance_widgets.db.WeatherRepository
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class GetForecastUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository,
    @AppCoroutineScope private val coroutineScope: CoroutineScope
) {
    // Define the method to get the forecast data
    suspend operator fun invoke() = with(coroutineScope) {
        // Implementation for fetching the forecast data from the repository
        // This could involve calling a method on weatherRepository to get the data
        // For example:
        // return weatherRepository.getForecast()
    }
}