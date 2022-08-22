package io.farafonova.weatherapp.ui

import android.util.Log
import androidx.lifecycle.*
import io.farafonova.weatherapp.domain.model.Location
import io.farafonova.weatherapp.persistence.WeatherDatasourceManager
import io.farafonova.weatherapp.domain.model.CurrentForecastWithLocation
import io.farafonova.weatherapp.domain.model.BriefCurrentForecastWithLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class WeatherApplicationViewModel(private val datasourceManager: WeatherDatasourceManager) :
    ViewModel() {

    val searchResult by lazy { MutableStateFlow<List<Location>?>(null) }
    val singleDetailedForecast by lazy { MutableStateFlow<CurrentForecastWithLocation?>(null) }
    val errorMessage by lazy { MutableSharedFlow<String>() }
    val isLongTaskRunning by lazy { MutableStateFlow(false) }

    suspend fun getFavorites(): Flow<List<BriefCurrentForecastWithLocation>?>? {
        return executeLongTask(
                { datasourceManager.getLatestFavoriteForecasts() },
                { printErrorMessageToLogAndShowItToUser(it) }
            )?.flowOn(Dispatchers.IO)
    }

    fun searchForLocations(locationName: String) = viewModelScope.launch(Dispatchers.IO) {
        executeLongTask({
            searchResult.value = datasourceManager.findLocationsByName(locationName)
        }, {
            printErrorMessageToLogAndShowItToUser(it)
        })
    }

    fun clearSearchResult() {
        searchResult.value = null
    }

    fun addOrRemoveFromFavorites(location: Location, shouldBeFavorite: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            location.inFavorites = shouldBeFavorite
            datasourceManager.changeFavoriteLocationState(location)
        }

    suspend fun getCurrentForecastForSpecificLocation(latitude: Float, longitude: Float) =
        viewModelScope.launch(Dispatchers.IO) {
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
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherApplicationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherApplicationViewModel(datasourceManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}