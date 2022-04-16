package io.farafonova.weatherapp.persistence

import io.farafonova.weatherapp.persistence.database.CurrentForecastEntity
import io.farafonova.weatherapp.persistence.database.ForecastDao
import io.farafonova.weatherapp.persistence.database.LocationEntity
import io.farafonova.weatherapp.persistence.network.geocoding.GeocodingRepository
import io.farafonova.weatherapp.ui.search.LocationSearchEntry
import io.farafonova.weatherapp.persistence.network.weather.WeatherRepository
import kotlinx.coroutines.flow.Flow

class WeatherDatasourceManager(
    private val dao: ForecastDao,
    private val apiKey: String,
    private val oneCallApiBaseUrl: String,
    private val geocodingApiBaseUrl: String
) {
    private val weatherRepository: WeatherRepository by lazy {
        WeatherRepository(oneCallApiBaseUrl, apiKey)
    }

    private val geocodingRepository: GeocodingRepository by lazy {
        GeocodingRepository(geocodingApiBaseUrl, apiKey)
    }

    val favoriteForecasts: Flow<Map<LocationEntity, CurrentForecastEntity>> =
        dao.getCurrentForecastForAllFavoriteLocations()

    suspend fun findLocationsByName(name: String): List<LocationSearchEntry>? {
        return geocodingRepository.getLocationByName(name)
            ?.map {
                val location = dao.getAllFavoriteLocations()
                    .find { locationEntity -> locationEntity.latitude == it.latitude.toFloat()
                            && locationEntity.longitude == it.longitude.toFloat() }
                LocationSearchEntry(
                    it.latitude,
                    it.longitude,
                    it.name,
                    it.state ?: "",
                    it.countryCode,
                    location?.inFavorites ?: false
                )
            }
    }

    suspend fun changeFavoriteLocationState(
        location: LocationSearchEntry,
        shouldBeFavorite: Boolean
    ) {
        val entity = LocationEntity(
            location.latitude.toFloat(),
            location.longitude.toFloat(),
            location.name,
            location.country,
            shouldBeFavorite
        )

        val isLocationInDb = dao.isThereAlreadySuchLocation(
            location.latitude.toFloat(),
            location.longitude.toFloat()
        )

        if (isLocationInDb) {
            dao.updateLocations(entity)
        } else {
            dao.insertLocations(entity)
        }
    }

    suspend fun removeLocationFromFavorites(location: LocationSearchEntry) {

    }
}