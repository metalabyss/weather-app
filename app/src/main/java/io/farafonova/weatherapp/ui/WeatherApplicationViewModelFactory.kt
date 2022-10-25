package io.farafonova.weatherapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.work.WorkInfo
import io.farafonova.weatherapp.domain.usecase.DefineIsItLightOutsideUseCase
import io.farafonova.weatherapp.persistence.ForecastWithLocationRepository
import io.farafonova.weatherapp.ui.current_forecast.CurrentForecastViewModel
import io.farafonova.weatherapp.ui.favorites.WeatherFavoritesViewModel
import io.farafonova.weatherapp.ui.search.LocationSearchViewModel

class WeatherApplicationViewModelFactory(
    private val repository: ForecastWithLocationRepository,
    private val refreshWorkInfo: LiveData<WorkInfo?>
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherFavoritesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherFavoritesViewModel(
                repository,
                refreshWorkInfo
            ) as T

        } else if (modelClass.isAssignableFrom(LocationSearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LocationSearchViewModel(repository) as T

        } else if (modelClass.isAssignableFrom(CurrentForecastViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CurrentForecastViewModel(
                repository,
                DefineIsItLightOutsideUseCase(repository)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}