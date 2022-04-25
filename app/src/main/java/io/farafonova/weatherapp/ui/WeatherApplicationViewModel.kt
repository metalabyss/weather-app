package io.farafonova.weatherapp.ui

import android.util.Log
import androidx.lifecycle.*
import io.farafonova.weatherapp.ui.search.LocationSearchEntry
import io.farafonova.weatherapp.persistence.WeatherDatasourceManager
import io.farafonova.weatherapp.ui.favorites.FavoritesWeatherEntry
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WeatherApplicationViewModel(private val datasourceManager: WeatherDatasourceManager) :
    ViewModel() {

    val searchResult: MutableLiveData<List<LocationSearchEntry>> = MutableLiveData()
    val errorMessage: MutableLiveData<String> = MutableLiveData("")

    suspend fun getFavorites(): StateFlow<List<FavoritesWeatherEntry>?> {
        return datasourceManager.getLatestFavoriteForecasts()
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)
    }

    fun searchForLocations(locationName: String) = viewModelScope.launch {
        try {
            searchResult.value = datasourceManager.findLocationsByName(locationName)
            if (searchResult.value.isNullOrEmpty()) {
                errorMessage.value = "Cannot find place with name $locationName."
            }
        } catch (throwable: Throwable) {
            Log.e(
                WeatherApplicationViewModel::class.qualifiedName,
                "${throwable::class.simpleName}: ${throwable.message}"
            )
            errorMessage.value = throwable.message
        }
    }

    fun addToFavorites(location: LocationSearchEntry) = viewModelScope.launch {
        datasourceManager.changeFavoriteLocationState(location, true)
    }

    fun removeFromFavorites(location: LocationSearchEntry) = viewModelScope.launch {
        datasourceManager.changeFavoriteLocationState(location, false)
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