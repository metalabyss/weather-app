package io.farafonova.weatherapp.ui.current_forecast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.farafonova.weatherapp.domain.model.BriefDailyForecastWithLocation
import io.farafonova.weatherapp.domain.model.CurrentForecastWithLocation
import io.farafonova.weatherapp.domain.model.HourlyForecastWithLocation
import io.farafonova.weatherapp.domain.usecase.DefineIsItLightOutsideUseCase
import io.farafonova.weatherapp.persistence.ForecastWithLocationRepository
import io.farafonova.weatherapp.ui.printErrorMessageToLogAndShowItToUser
import io.farafonova.weatherapp.ui.runSuspendFunctionWithProgressIndicator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class CurrentForecastViewModel(
    private val repository: ForecastWithLocationRepository,
    private val lightOutsideUseCase: DefineIsItLightOutsideUseCase
) : ViewModel() {

    val isLongTaskRunning by lazy { MutableStateFlow(false) }
    val errorMessage by lazy { MutableSharedFlow<String>() }
    val singleDetailedForecast by lazy { MutableStateFlow<CurrentForecastWithLocation?>(null) }
    val hourlyForecast by lazy { MutableStateFlow<List<HourlyForecastWithLocation>>(emptyList()) }
    val dailyForecasts by lazy { MutableStateFlow<List<BriefDailyForecastWithLocation>>(emptyList()) }

    suspend fun getCurrentForecastForSpecificLocation(latitude: Double, longitude: Double) =
        viewModelScope.launch(Dispatchers.IO) {
            runSuspendFunctionWithProgressIndicator(isLongTaskRunning, {
                repository.getCurrentForecastForSpecificLocation(latitude, longitude)
                    .collect { forecast -> singleDetailedForecast.value = forecast }
            }, { throwable ->
                TAG?.let { printErrorMessageToLogAndShowItToUser(it, throwable, errorMessage) }
            })
        }

    suspend fun getHourlyForecastForSpecificLocation(latitude: Double, longitude: Double) =
        viewModelScope.launch(Dispatchers.IO) {
            runSuspendFunctionWithProgressIndicator(isLongTaskRunning, {
                repository.getHourlyForecastForSpecificLocation(latitude, longitude)
                    .collect { list -> hourlyForecast.value = list }
            }, { throwable ->
                TAG?.let { printErrorMessageToLogAndShowItToUser(it, throwable, errorMessage) }
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
                repository.getDailyForecastsForSpecificLocation(latitude, longitude)
                    .collect { list -> dailyForecasts.value = list }
            }, { throwable ->
                TAG?.let { printErrorMessageToLogAndShowItToUser(it, throwable, errorMessage) }
            })
        }

    companion object {
        private val TAG = CurrentForecastViewModel::class.qualifiedName
    }
}