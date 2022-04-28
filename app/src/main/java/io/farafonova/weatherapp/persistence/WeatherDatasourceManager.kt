package io.farafonova.weatherapp.persistence

import android.content.SharedPreferences
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
    private val geocodingApiBaseUrl: String,
    private val lastSyncSharedPrefs: SharedPreferences,
    private val lastSyncTimeSharedPrefsKey: String
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
        val latitude = location.latitude.toFloat()
        val longitude = location.longitude.toFloat()

        val entity = LocationEntity(
            latitude,
            longitude,
            location.name,
            location.country,
            shouldBeFavorite
        )

        val isLocationInDb = dao.isThereAlreadySuchLocation(latitude, longitude)

        if (isLocationInDb) {
            dao.updateLocations(entity)
        } else {
            dao.insertLocations(entity)
        }

        if (shouldBeFavorite) {
            val forecastEntity = downloadCurrentForecastAndConvertItToEntity(latitude, longitude)
            dao.insertCurrentForecast(forecastEntity)
        }
    }

    suspend fun getLatestFavoriteForecasts(): Flow<List<FavoritesWeatherEntry>> {
        val locations = dao.getAllFavoriteLocations()
        if (locations.isEmpty()) {
            return flow { emptyList<FavoritesWeatherEntry>() }
        }

        val lastSyncTime = lastSyncSharedPrefs.getLong(lastSyncTimeSharedPrefsKey, 0L)
        val currentTime = System.currentTimeMillis()

        if (lastSyncTime == 0L || currentTime - lastSyncTime > 18000000L) {

            val downloadedForecasts = locations.map { location ->
                downloadCurrentForecastAndConvertItToEntity(location.latitude, location.longitude)
            }
            dao.insertCurrentForecast(*downloadedForecasts.toTypedArray())

            with(lastSyncSharedPrefs.edit()) {
                putLong(lastSyncTimeSharedPrefsKey, System.currentTimeMillis())
                apply()
            }
        }

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

    private suspend fun downloadCurrentForecastAndConvertItToEntity(
        latitude: Float,
        longitude: Float
    ): CurrentForecastEntity {

        val overallWeatherResponse =
            weatherRepository.getWeather(latitude, longitude)
        val currentWeatherResponse = overallWeatherResponse?.currentWeatherResponse

        return CurrentForecastEntity(
            latitude,
            longitude,
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
}