package io.farafonova.weatherapp.ui

import android.util.Log
import androidx.lifecycle.*
import io.farafonova.weatherapp.ui.search.LocationSearchEntry
import io.farafonova.weatherapp.persistence.WeatherDatasourceManager
import io.farafonova.weatherapp.ui.current_forecast.CurrentForecastData
import io.farafonova.weatherapp.ui.favorites.FavoritesWeatherEntry
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WeatherApplicationViewModel(private val datasourceManager: WeatherDatasourceManager) :
    ViewModel() {

    val searchResult by lazy { MutableStateFlow<List<LocationSearchEntry>?>(null) }
    val singleDetailedForecast by lazy { MutableStateFlow<CurrentForecastData?>(null) }
    val errorMessage by lazy { MutableSharedFlow<String>() }
    val isLongTaskRunning by lazy { MutableStateFlow(false) }

    suspend fun getFavorites(): StateFlow<List<FavoritesWeatherEntry>?>? {
        return executeLongTask({ datasourceManager.getLatestFavoriteForecasts() },
            {
                printErrorMessageToLogAndShowItToUser(it)
            })?.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            null
        )
    }

    fun searchForLocations(locationName: String) = viewModelScope.launch {
        executeLongTask({
            searchResult.value = datasourceManager.findLocationsByName(locationName)
        }, {
            printErrorMessageToLogAndShowItToUser(it)
        })
    }

    fun clearSearchResult() {
        searchResult.value = null
    }

    fun addToFavorites(location: LocationSearchEntry) = viewModelScope.launch {
        datasourceManager.changeFavoriteLocationState(location, true)
    }

    fun removeFromFavorites(location: LocationSearchEntry) = viewModelScope.launch {
        datasourceManager.changeFavoriteLocationState(location, false)
    }

    suspend fun getCurrentForecastForSpecificLocation(latitude: String, longitude: String) =
        viewModelScope.launch {
            executeLongTask({
                datasourceManager.getCurrentForecastForSpecificLocation(latitude, longitude)
                    .collect { forecast -> singleDetailedForecast.value = forecast }
            }, {
                printErrorMessageToLogAndShowItToUser(it)
            })
        }

    private suspend fun <T> executeLongTask(
        task: suspend () -> T,
        exceptionHandler: suspend (Throwable) -> Unit
    ): T? {
        isLongTaskRunning.value = true
        val result = try {
            task.invoke()
        } catch (throwable: Throwable) {
            exceptionHandler.invoke(throwable)
            null
        }
        isLongTaskRunning.value = false
        return result
    }

    private suspend fun printErrorMessageToLogAndShowItToUser(throwable: Throwable) {
        throwable.localizedMessage?.let { e -> errorMessage.emit(e) }
        Log.e(
            WeatherApplicationViewModel::class.qualifiedName,
            "${throwable::class.simpleName}: ${throwable.message}"
        )
    }
}

class WeatherApplicationViewModelFactory(private val datasourceManager: WeatherDatasourceManager) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherApplicationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherApplicationViewModel(datasourceManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}