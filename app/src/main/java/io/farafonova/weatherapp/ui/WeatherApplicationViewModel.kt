package io.farafonova.weatherapp.ui

import android.util.Log
import androidx.lifecycle.*
import io.farafonova.weatherapp.domain.model.Location
import io.farafonova.weatherapp.persistence.ForecastWithLocationRepository
import io.farafonova.weatherapp.domain.model.CurrentForecastWithLocation
import io.farafonova.weatherapp.domain.model.BriefCurrentForecastWithLocation
import io.farafonova.weatherapp.domain.model.BriefDailyForecastWithLocation
import io.farafonova.weatherapp.domain.model.HourlyForecastWithLocation
import io.farafonova.weatherapp.domain.usecase.DefineIsItLightOutsideUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class WeatherApplicationViewModel(
    private val datasourceManager: ForecastWithLocationRepository,
    private val lightOutsideUseCase: DefineIsItLightOutsideUseCase,
) : ViewModel() {

    val searchResult by lazy { MutableStateFlow<List<Location>?>(null) }
    val favoritesList by lazy { MutableStateFlow<List<BriefCurrentForecastWithLocation>>(emptyList()) }
    val singleDetailedForecast by lazy { MutableStateFlow<CurrentForecastWithLocation?>(null) }
    val hourlyForecast by lazy { MutableStateFlow<List<HourlyForecastWithLocation>>(emptyList()) }
    val dailyForecasts by lazy { MutableStateFlow<List<BriefDailyForecastWithLocation>>(emptyList()) }
    val errorMessage by lazy { MutableSharedFlow<String>() }
    val isLongTaskRunning by lazy { MutableStateFlow(false) }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            executeLongTask(
                { datasourceManager.getLatestFavoriteForecasts().collect { list -> favoritesList.value = list } },
                { printErrorMessageToLogAndShowItToUser(it) }
            )
        }
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

    suspend fun getCurrentForecastForSpecificLocation(latitude: Double, longitude: Double) =
        viewModelScope.launch(Dispatchers.IO) {
            executeLongTask({
                datasourceManager.getCurrentForecastForSpecificLocation(latitude, longitude)
                    .collect { forecast -> singleDetailedForecast.value = forecast }
            }, {
                printErrorMessageToLogAndShowItToUser(it)
            })
        }

    suspend fun getHourlyForecastForSpecificLocation(latitude: Double, longitude: Double) =
        viewModelScope.launch(Dispatchers.IO) {
            executeLongTask({
                datasourceManager.getHourlyForecastForSpecificLocation(latitude, longitude)
                    .collect { list -> hourlyForecast.value = list }
            }, {
                printErrorMessageToLogAndShowItToUser(it)
            })
        }

    suspend fun isItLight(moment: Long): Boolean {
        return viewModelScope.async(Dispatchers.IO) {
            singleDetailedForecast.value?.let {
                val location = it.location
                return@async lightOutsideUseCase(
                    location.latitude,
                    location.longitude,
                    moment,
                    location.timeZoneOffset
                )
            } ?: false
        }.await()
    }

    suspend fun getDailyForecastsForSpecificLocation(latitude: Double, longitude: Double) =
        viewModelScope.launch(Dispatchers.IO) {
            executeLongTask({
                datasourceManager.getDailyForecastsForSpecificLocation(latitude, longitude)
                    .collect { list -> dailyForecasts.value = list }
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

class WeatherApplicationViewModelFactory(private val datasourceManager: ForecastWithLocationRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherApplicationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherApplicationViewModel(
                datasourceManager,
                DefineIsItLightOutsideUseCase(datasourceManager)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}