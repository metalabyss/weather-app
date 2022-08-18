package io.farafonova.weatherapp.persistence

import android.content.SharedPreferences
import io.farafonova.weatherapp.persistence.database.ForecastDao
import io.farafonova.weatherapp.persistence.network.geocoding.GeocodingRepository
import io.farafonova.weatherapp.domain.model.Location
import io.farafonova.weatherapp.persistence.network.weather.WeatherRepository
import io.farafonova.weatherapp.persistence.database.CurrentForecastEntity
import io.farafonova.weatherapp.domain.model.CurrentForecastWithLocation
import io.farafonova.weatherapp.domain.model.BriefCurrentForecastWithLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*

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

    suspend fun findLocationsByName(name: String): List<Location>? {
        return geocodingRepository.getLocationByName(name)
            ?.map {
                val inFavorites =
                    dao.isLocationAlreadyInFavorites(it.latitude.toFloat(), it.longitude.toFloat())
                val searchEntry = it.toLocationModel()
                searchEntry.inFavorites = inFavorites
                searchEntry
            }
    }

    suspend fun changeFavoriteLocationState(location: Location) {
        val entity = location.toLocationEntity()
        val isLocationInDb = dao.isThereAlreadySuchLocation(entity.latitude, entity.longitude)

        if (isLocationInDb) {
            dao.updateLocations(entity)
        } else {
            dao.insertLocations(entity)
        }

        if (entity.inFavorites) {
            val forecastEntity = getLatestCurrentForecast(entity.latitude, entity.longitude)
            dao.insertCurrentForecast(forecastEntity)
        }
    }

    suspend fun getLatestFavoriteForecasts(): Flow<List<BriefCurrentForecastWithLocation>> {
        val locations = dao.getAllFavoriteLocations()
        if (locations.isEmpty()) {
            return flow { emptyList<BriefCurrentForecastWithLocation>() }
        }

        val lastSyncTime = lastSyncSharedPrefs.getLong(lastSyncTimeSharedPrefsKey, 0L)
        val currentTime = System.currentTimeMillis()

        if (lastSyncTime == 0L || currentTime - lastSyncTime > 1800000L) {

            val downloadedForecasts = locations.map { location ->
                getLatestCurrentForecast(location.latitude, location.longitude)
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

                    BriefCurrentForecastWithLocation(
                        forecast.temperature.toInt(),
                        location.toLocationModel()
                    )
                }
            }

        return forecastsFlow
    }

    private suspend fun getLatestCurrentForecast(
        latitude: Float,
        longitude: Float
    ): CurrentForecastEntity {

        val overallWeatherResponse =
            weatherRepository.getWeather(latitude, longitude)

        return overallWeatherResponse!!.currentWeatherResponse.toCurrentForecastEntity(
            latitude,
            longitude
        )
    }

    suspend fun getCurrentForecastForSpecificLocation(
        latitude: Float,
        longitude: Float
    ): Flow<CurrentForecastWithLocation?> {
        val location = dao.getSpecificLocation(latitude, longitude)

        val currentForecast =
            dao.getCurrentForecastForSpecificLocation(latitude, longitude)

        val currentForecastWithLocation = currentForecast?.let {
            val forecastTime = Instant.ofEpochSecond(it.forecastTime.toLong())
                .atZone(TimeZone.getDefault().toZoneId())

            val formatter = DateTimeFormatter.ofPattern("dd.MM, HH:mm")

            location?.let {
                CurrentForecastWithLocation(
                    currentForecast.temperature.toInt(),
                    currentForecast.feelsLikeTemperature.toInt(),
                    forecastTime.format(formatter),
                    currentForecast.windSpeed,
                    currentForecast.windDegree,
                    currentForecast.pressure,
                    currentForecast.humidity,
                    currentForecast.dewPoint.toInt(),
                    currentForecast.uvi,
                    currentForecast.description,
                    location.toLocationModel()
                )
            }
        }

        return flowOf(currentForecastWithLocation)
    }
}