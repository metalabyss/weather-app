package io.farafonova.weatherapp

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class WeatherApplicationViewModel(private val datasourceManager: WeatherDatasourceManager) :
    ViewModel() {
    val favorites: MutableLiveData<List<FavoritesWeatherEntry>> = MutableLiveData(
        /*listOf(
            FavoritesWeatherEntry(
                locationName = "Novosibirsk",
                temperature = -8,
                latitude = "54.96781445",
                longitude = "82.95159894278376"
            )
        )*/
    )
    val searchResult: MutableLiveData<List<LocationSearchEntry>> = MutableLiveData()
    val errorMessage: MutableLiveData<String> = MutableLiveData("")

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