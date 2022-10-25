package io.farafonova.weatherapp.ui.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import io.farafonova.weatherapp.domain.model.BriefCurrentForecastWithLocation
import io.farafonova.weatherapp.domain.model.Location
import io.farafonova.weatherapp.persistence.ForecastWithLocationRepository
import io.farafonova.weatherapp.ui.printErrorMessageToLogAndShowItToUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class WeatherFavoritesViewModel(
    private val repository: ForecastWithLocationRepository,
    val refreshWorkInfo: LiveData<WorkInfo?>
) : ViewModel() {

    val favoritesList by lazy { MutableStateFlow<List<BriefCurrentForecastWithLocation>>(emptyList()) }
    val errorMessage by lazy { MutableSharedFlow<String>() }
    val isLongTaskRunning by lazy { MutableStateFlow(false) }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.getLatestFavoriteForecasts()
                    .collect { list -> favoritesList.value = list }
            } catch (throwable: Throwable) {
                TAG?.let { printErrorMessageToLogAndShowItToUser(it, throwable, errorMessage) }
            }
        }
    }

    fun addOrRemoveFromFavorites(location: Location, shouldBeFavorite: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            location.inFavorites = shouldBeFavorite
            repository.changeFavoriteLocationState(location)
        }

    companion object {
        private val TAG = WeatherFavoritesViewModel::class.qualifiedName
    }
}
