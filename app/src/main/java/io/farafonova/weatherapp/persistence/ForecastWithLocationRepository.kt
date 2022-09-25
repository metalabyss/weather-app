package io.farafonova.weatherapp.persistence

import io.farafonova.weatherapp.domain.model.BriefCurrentForecastWithLocation
import io.farafonova.weatherapp.domain.model.BriefDailyForecastWithLocation
import io.farafonova.weatherapp.domain.model.CurrentForecastWithLocation
import io.farafonova.weatherapp.domain.model.DailyForecast
import io.farafonova.weatherapp.domain.model.HourlyForecastWithLocation
import io.farafonova.weatherapp.domain.model.Location
import io.farafonova.weatherapp.persistence.database.ForecastDao
import io.farafonova.weatherapp.persistence.database.LocationEntity
import io.farafonova.weatherapp.persistence.network.geocoding.GeocodingDataSource
import io.farafonova.weatherapp.persistence.network.weather.WeatherDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.temporal.ChronoUnit

class ForecastWithLocationRepository(private val dao: ForecastDao) {

    suspend fun findLocationsByName(name: String): List<Location>? {
        return GeocodingDataSource.getLocationByName(name)
            ?.map {
                val inFavorites =
                    dao.isLocationAlreadyInFavorites(
                        it.latitude.toDouble(),
                        it.longitude.toDouble()
                    )
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
            downloadLatestForecastsAndSaveToDb(listOf(entity))
        }
    }

    fun getLatestFavoriteForecasts(): Flow<List<BriefCurrentForecastWithLocation>> {
        return dao.getCurrentForecastForAllFavoriteLocations()
            .map {
                it.map { entry ->
                    val location = entry.key
                    val forecast = entry.value
                    forecast.toBriefCurrentForecastWithLocation(location.toLocationModel())
                }
            }
    }

    private suspend fun downloadLatestForecastsAndSaveToDb(locations: List<LocationEntity>) {

        locations.forEach { location ->
            val latitude = location.latitude
            val longitude = location.longitude

            val overallWeatherResponse =
                WeatherDataSource.getWeather(latitude, longitude)

            overallWeatherResponse?.let { response ->
                if (location.timezoneOffset != response.timezoneOffset) {
                    val updatedLocation = location.copy(timezoneOffset = response.timezoneOffset)
                    dao.updateLocations(updatedLocation)
                    println("Previous location: $location")
                    println("Updated location: $updatedLocation")
                }
                val currentForecastEntity = response.currentWeatherResponse
                    .toCurrentForecastEntity(latitude, longitude)

                val hourlyForecastEntities = response.hourlyWeather.map {
                    it.toHourlyForecastEntity(latitude, longitude)
                }
                val dailyForecastEntities = response.dailyWeather.map {
                    it.toDailyForecastEntity(latitude, longitude)
                }

                dao.insertOverallForecast(
                    currentForecastEntity, hourlyForecastEntities, dailyForecastEntities
                )
            }
        }
    }

    suspend fun refreshAllFavoriteForecastsFromRemote() {
        val locations = dao.getAllFavoriteLocations()
        downloadLatestForecastsAndSaveToDb(locations)
    }

    suspend fun getCurrentForecastForSpecificLocation(
        latitude: Double,
        longitude: Double
    ): Flow<CurrentForecastWithLocation?> {
        val location = dao.getSpecificLocation(latitude, longitude)

        val currentForecast =
            dao.getCurrentForecastForSpecificLocation(latitude, longitude)

        val currentForecastWithLocation = currentForecast?.let {
            location?.let {
                currentForecast.toCurrentForecastWithLocation(location.toLocationModel())
            }
        }

        return flowOf(currentForecastWithLocation)
    }

    suspend fun getHourlyForecastForSpecificLocation(
        latitude: Double,
        longitude: Double
    ): Flow<List<HourlyForecastWithLocation>> {

        return flow {
            val currentHour = Instant.now().truncatedTo(ChronoUnit.HOURS).epochSecond
            val location = dao.getSpecificLocation(latitude, longitude)?.toLocationModel()

            if (location != null) {
                emit(dao.getHourlyForecastForSpecificLocation(latitude, longitude, currentHour)
                    .map { it.toHourlyForecastWithLocationModel(location) })
            }
        }
    }

    suspend fun getDailyForecast(
        latitude: Double,
        longitude: Double,
        dayStartTime: Long,
        dayEndTime: Long
    ): Flow<DailyForecast?> {
        return flow {
            val daily = dao.getDailyForecasts(latitude, longitude, dayStartTime, dayEndTime)
                .firstOrNull()
                ?.toDailyForecastModel()
            emit(daily)
        }
    }

    suspend fun getDailyForecastsForSpecificLocation(
        latitude: Double,
        longitude: Double
    ): Flow<List<BriefDailyForecastWithLocation>> {
        return flow {
            val location = dao.getSpecificLocation(latitude, longitude)?.toLocationModel()

            if (location != null) {
                emit(dao.getDailyForecasts(latitude, longitude)
                    .map { it.toBriefDailyForecastWithLocation(location) })
            }
        }
    }
}