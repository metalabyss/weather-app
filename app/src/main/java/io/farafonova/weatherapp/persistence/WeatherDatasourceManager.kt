package io.farafonova.weatherapp.persistence

import android.content.SharedPreferences
import io.farafonova.weatherapp.persistence.database.ForecastDao
import io.farafonova.weatherapp.persistence.network.geocoding.GeocodingRepository
import io.farafonova.weatherapp.domain.model.Location
import io.farafonova.weatherapp.persistence.network.weather.WeatherRepository
import io.farafonova.weatherapp.domain.model.CurrentForecastWithLocation
import io.farafonova.weatherapp.domain.model.BriefCurrentForecastWithLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
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
            downloadLatestForecastsAndSaveToDb(listOf(Pair(entity.latitude, entity.longitude)))
        }
    }

    suspend fun getLatestFavoriteForecasts(fetchFromRemote: Boolean = false): Flow<List<BriefCurrentForecastWithLocation>> {
        return flow {
            var listOfForecasts = dao.getCurrentForecastForAllFavoriteLocations()
                .map { entry ->
                    val location = entry.key
                    val forecast = entry.value

                    BriefCurrentForecastWithLocation(
                        forecast.temperature.toInt(),
                        location.toLocationModel()
                    )
                }
            emit(listOfForecasts)

            val lastSyncTime = lastSyncSharedPrefs.getLong(lastSyncTimeSharedPrefsKey, 0L)
            val currentTime = System.currentTimeMillis()
            val updateWasLongAgo = lastSyncTime == 0L || currentTime - lastSyncTime > 1800000L

            if (fetchFromRemote || updateWasLongAgo) {
                val coordinates = dao
                    .getAllFavoriteLocations()
                    .map { location -> Pair(location.latitude, location.longitude) }

                downloadLatestForecastsAndSaveToDb(coordinates)

                listOfForecasts = dao.getCurrentForecastForAllFavoriteLocations()
                    .map { entry ->
                        val location = entry.key
                        val forecast = entry.value

                        BriefCurrentForecastWithLocation(
                            forecast.temperature.toInt(),
                            location.toLocationModel()
                        )
                    }
                emit(listOfForecasts)

                with(lastSyncSharedPrefs.edit()) {
                    putLong(lastSyncTimeSharedPrefsKey, System.currentTimeMillis())
                    apply()
                }
            }
        }
    }

    private suspend fun downloadLatestForecastsAndSaveToDb(locations: List<Pair<Float, Float>>) {

        locations.forEach {
            val latitude = it.first
            val longitude = it.second

            val overallWeatherResponse =
                weatherRepository.getWeather(latitude, longitude)

            dao.insertCurrentForecast(overallWeatherResponse!!.currentWeatherResponse.toCurrentForecastEntity(
                latitude,
                longitude
            ))
        }
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