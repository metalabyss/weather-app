package io.farafonova.weatherapp.ui.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import io.farafonova.weatherapp.R
import io.farafonova.weatherapp.domain.model.BriefCurrentForecastWithLocation
import io.farafonova.weatherapp.domain.model.Location
import io.farafonova.weatherapp.persistence.ForecastWithLocationRepository
import io.farafonova.weatherapp.ui.SnackbarOptions
import io.farafonova.weatherapp.ui.UiText
import io.farafonova.weatherapp.ui.printErrorMessageToLogAndShowItToUser
import io.farafonova.weatherapp.ui.runSuspendFunctionWithProgressIndicator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class WeatherFavoritesViewModel(
    private val repository: ForecastWithLocationRepository,
    val refreshWorkInfo: LiveData<WorkInfo?>
) : ViewModel() {

    val favoritesList by lazy { MutableStateFlow<List<BriefCurrentForecastWithLocation>>(emptyList()) }
    val snackbarOptions by lazy { MutableSharedFlow<SnackbarOptions>() }
    val errorMessage by lazy { MutableSharedFlow<String>() }
    val isLongTaskRunning by lazy { MutableStateFlow(false) }
    val favoritesState by lazy { MutableStateFlow(WeatherFavoritesState.EMPTY) }

    init { getLatestCachedForecasts() }

    private fun getLatestCachedForecasts() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.getLatestFavoriteForecasts().collect { list ->
                    favoritesState.value =
                        if (list.isEmpty()) {
                            WeatherFavoritesState.EMPTY
                        } else {
                            WeatherFavoritesState.UP_TO_DATE
                        }
                    favoritesList.value = list
                }
            } catch (throwable: Throwable) {
                TAG?.let { printErrorMessageToLogAndShowItToUser(it, throwable, errorMessage) }
            }
        }
    }

    fun refreshForecasts() = viewModelScope.launch {
        runSuspendFunctionWithProgressIndicator(
            isProgressIndicatorShowing = isLongTaskRunning,
            task = { repository.refreshAllFavoriteForecastsFromRemote() },
            exceptionHandler = { throwable ->
                TAG?.let { printErrorMessageToLogAndShowItToUser(it, throwable, errorMessage) }
            }
        )
    }

    fun addOrRemoveFromFavorites(location: Location, shouldBeFavorite: Boolean) : Job =
        viewModelScope.launch(Dispatchers.IO) {
            location.inFavorites = shouldBeFavorite
            repository.changeFavoriteLocationState(location)

            val locationName = "${location.country.flag()} ${location.name}"
            val action: () -> Unit = { addOrRemoveFromFavorites(location, true) }

            val options = when (shouldBeFavorite) {
                false -> SnackbarOptions(
                    UiText.StringResource(R.string.message_remove_location, locationName),
                    UiText.StringResource(R.string.favorites_snackbar_undo),
                    action
                )
                true -> null
            }
            options?.let { snackbarOptions.emit(options) }
        }

    companion object {
        private val TAG = WeatherFavoritesViewModel::class.qualifiedName
    }
}
