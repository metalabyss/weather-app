package io.farafonova.weatherapp.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.farafonova.weatherapp.R
import io.farafonova.weatherapp.domain.model.Location
import io.farafonova.weatherapp.persistence.ForecastWithLocationRepository
import io.farafonova.weatherapp.ui.UiText
import io.farafonova.weatherapp.ui.printErrorMessageToLogAndShowItToUser
import io.farafonova.weatherapp.ui.runSuspendFunctionWithProgressIndicator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LocationSearchViewModel(
    private val repository: ForecastWithLocationRepository
) : ViewModel() {

    val searchResult by lazy { MutableStateFlow<List<Location>?>(null) }
    val isLongTaskRunning by lazy { MutableStateFlow(false) }
    val actionMessage by lazy { MutableSharedFlow<UiText>() }
    val errorMessage by lazy { MutableSharedFlow<String>() }

    fun searchForLocations(locationName: String) = viewModelScope.launch(Dispatchers.IO) {
        runSuspendFunctionWithProgressIndicator(isLongTaskRunning, {
            searchResult.value = repository.findLocationsByName(locationName)
        }, { throwable ->
            TAG?.let { printErrorMessageToLogAndShowItToUser(it, throwable, errorMessage) }
        })
    }

    fun clearSearchResult() {
        searchResult.value = null
    }

    fun addOrRemoveFromFavorites(location: Location, shouldBeFavorite: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            location.inFavorites = shouldBeFavorite
            repository.changeFavoriteLocationState(location)

            val locationName = "${location.country.flag()} ${location.name}"
            val notificationText = when (shouldBeFavorite) {
                true -> UiText.StringResource(R.string.message_add_location, locationName)
                false -> UiText.StringResource(R.string.message_remove_location, locationName)
            }
            actionMessage.emit(notificationText)
        }

    companion object {
        private val TAG = LocationSearchViewModel::class.qualifiedName
    }
}