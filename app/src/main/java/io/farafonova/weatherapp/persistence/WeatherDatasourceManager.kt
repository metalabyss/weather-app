package io.farafonova.weatherapp.persistence

import io.farafonova.weatherapp.persistence.database.CurrentForecastEntity
import io.farafonova.weatherapp.persistence.database.ForecastDao
import io.farafonova.weatherapp.persistence.database.LocationEntity
import io.farafonova.weatherapp.persistence.network.geocoding.GeocodingRepository
import io.farafonova.weatherapp.ui.search.LocationSearchEntry
import io.farafonova.weatherapp.persistence.network.weather.WeatherRepository
import io.farafonova.weatherapp.ui.favorites.FavoritesWeatherEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

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

    suspend fun findLocationsByName(name: String): List<LocationSearchEntry>? {
        return geocodingRepository.getLocationByName(name)
            ?.map {
                val location = dao.getAllFavoriteLocations()
                    .find { locationEntity ->
                        locationEntity.latitude == it.latitude.toFloat()
                                && locationEntity.longitude == it.longitude.toFloat()
                    }
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

    suspend fun getLatestFavoriteForecasts(): Flow<List<FavoritesWeatherEntry>> {
        val locations = dao.getAllFavoriteLocations()
        if (locations.isEmpty()) {
            return flow { emptyList<FavoritesWeatherEntry>() }
        }

        val downloadedForecasts = locations.map { location ->
            val overallWeatherResponse =
                weatherRepository.getWeather(
                    location.latitude,
                    location.longitude
                )
            val currentWeatherResponse = overallWeatherResponse?.currentWeatherResponse

            CurrentForecastEntity(
                location.latitude,
                location.longitude,
                currentWeatherResponse!!.currentTime,
                currentWeatherResponse.temperature,
                currentWeatherResponse.feelsLikeTemperature,
                currentWeatherResponse.windSpeed,
                currentWeatherResponse.windDegree,
                currentWeatherResponse.pressure,
                currentWeatherResponse.humidity,
                currentWeatherResponse.dewPoint,
                currentWeatherResponse.uvi,
                currentWeatherResponse.description[0].description
            )
        }

        dao.insertCurrentForecast(*downloadedForecasts.toTypedArray())

        val forecastsFlow = dao.getCurrentForecastForAllFavoriteLocations()
            .map { map ->
                map.map { entry ->
                    val location = entry.key
                    val forecast = entry.value

                    FavoritesWeatherEntry(
                        location.latitude.toString(),
                        location.longitude.toString(),
                        location.locationName,
                        forecast.temperature.toInt()
                    )
                }
            }

        return forecastsFlow
    }
}