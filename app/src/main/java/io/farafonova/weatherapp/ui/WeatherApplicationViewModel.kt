package io.farafonova.weatherapp.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import io.farafonova.weatherapp.domain.model.BriefCurrentForecastWithLocation
import io.farafonova.weatherapp.domain.model.BriefDailyForecastWithLocation
import io.farafonova.weatherapp.domain.model.CurrentForecastWithLocation
import io.farafonova.weatherapp.domain.model.HourlyForecastWithLocation
import io.farafonova.weatherapp.domain.model.Location
import io.farafonova.weatherapp.domain.usecase.DefineIsItLightOutsideUseCase
import io.farafonova.weatherapp.persistence.ForecastWithLocationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class WeatherApplicationViewModel(
    private val datasourceManager: ForecastWithLocationRepository,
    private val lightOutsideUseCase: DefineIsItLightOutsideUseCase,
    val refreshWorkInfo: LiveData<WorkInfo?>
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
            try {
                datasourceManager.getLatestFavoriteForecasts()
                    .collect { list -> favoritesList.value = list }
            } catch (throwable: Throwable) {
                printErrorMessageToLogAndShowItToUser(throwable)
            }
        }
    }

    fun searchForLocations(locationName: String) = viewModelScope.launch(Dispatchers.IO) {
        runSuspendFunctionWithProgressIndicator(isLongTaskRunning, {
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
            runSuspendFunctionWithProgressIndicator(isLongTaskRunning, {
                datasourceManager.getCurrentForecastForSpecificLocation(latitude, longitude)
                    .collect { forecast -> singleDetailedForecast.value = forecast }
            }, {
                printErrorMessageToLogAndShowItToUser(it)
            })
        }

    suspend fun getHourlyForecastForSpecificLocation(latitude: Double, longitude: Double) =
        viewModelScope.launch(Dispatchers.IO) {
            runSuspendFunctionWithProgressIndicator(isLongTaskRunning, {
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
            runSuspendFunctionWithProgressIndicator(isLongTaskRunning, {
                datasourceManager.getDailyForecastsForSpecificLocation(latitude, longitude)
                    .collect { list -> dailyForecasts.value = list }
            }, {
                printErrorMessageToLogAndShowItToUser(it)
            })
        }

    private suspend fun printErrorMessageToLogAndShowItToUser(throwable: Throwable) {
        throwable.localizedMessage?.let { e -> errorMessage.emit(e) }
        Log.e(
            WeatherApplicationViewModel::class.qualifiedName,
            "${throwable::class.simpleName}: ${throwable.message}"
        )
    }
}

class WeatherApplicationViewModelFactory(
    private val datasourceManager: ForecastWithLocationRepository,
    private val refreshWorkInfo: LiveData<WorkInfo?>
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherApplicationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherApplicationViewModel(
                datasourceManager,
                DefineIsItLightOutsideUseCase(datasourceManager),
                refreshWorkInfo
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}