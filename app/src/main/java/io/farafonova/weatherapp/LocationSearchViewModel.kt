package io.farafonova.weatherapp

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class LocationSearchViewModel(private val datasourceManager: WeatherDatasourceManager) :
    ViewModel() {
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
                    LocationSearchViewModel::class.qualifiedName,
                    "${throwable::class.simpleName}: ${throwable.message}"
                )
                errorMessage.value = throwable.message
            }
        }
}

class LocationSearchViewModelFactory(private val datasourceManager: WeatherDatasourceManager) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LocationSearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LocationSearchViewModel(datasourceManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}