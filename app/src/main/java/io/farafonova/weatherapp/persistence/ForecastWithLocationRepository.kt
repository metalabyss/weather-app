package io.farafonova.weatherapp.persistence

import android.content.SharedPreferences
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

class ForecastWithLocationRepository(
    private val dao: ForecastDao,
    private val sharedPrefs: SharedPreferences,
    private val lastSyncTimeSharedPrefsKey: String
) {

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

    suspend fun getLatestFavoriteForecasts(): Flow<List<BriefCurrentForecastWithLocation>> {
        val lastSyncTime = sharedPrefs.getLong(lastSyncTimeSharedPrefsKey, 0L)
        val now = Instant.now().epochSecond

        if (lastSyncTime < now && now < nextHourFromEpochInSeconds(lastSyncTime)) {
            return dao.getCurrentForecastForAllFavoriteLocations()
                .map {
                    it.map { entry ->
                        val location = entry.key
                        val forecast = entry.value
                        forecast.toBriefCurrentForecastWithLocation(location.toLocationModel())
                    }
                }
        } else {
            return dao.getAllFavoriteLocationsAsFlow().map { it ->
                it.flatMap { location ->
                    val currentHour = currentHourFromEpochInSeconds()
                    dao.getHourlyForecastForSpecificLocation(
                        location.latitude, location.longitude,
                        currentHour, 1
                    ).mapNotNull { entity ->
                        val sunriseAndSunset = getSunriseAndSunsetInLocationOnParticularDay(
                            location.latitude,
                            location.longitude,
                            currentHour,
                            location.timezoneOffset
                        )
                        sunriseAndSunset?.let { pair ->
                            entity.toBriefCurrentForecastWithLocation(
                                sunriseTime = pair.first,
                                sunsetTime = pair.second,
                                location.toLocationModel()
                            )
                        }
                    }
                }
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
    ): Flow<CurrentForecastWithLocation?> = flow {
        val location = dao.getSpecificLocation(latitude, longitude)

        val lastSyncTime = sharedPrefs.getLong(lastSyncTimeSharedPrefsKey, 0L)
        val now = Instant.now().epochSecond

        location?.let {
            if (lastSyncTime < now && now < nextHourFromEpochInSeconds(lastSyncTime)) {
                val currentForecast =
                    dao.getCurrentForecastForSpecificLocation(latitude, longitude)

                val currentForecastWithLocation = currentForecast?.let {
                    currentForecast.toCurrentForecastWithLocation(location.toLocationModel())
                }
                emit(currentForecastWithLocation)

            } else {
                val currentHour = currentHourFromEpochInSeconds()

                val hourlyForecastEntity =
                    dao.getHourlyForecastForSpecificLocation(
                        it.latitude, it.longitude,
                        currentHour, 1
                    ).firstOrNull()

                val sunriseAndSunset =
                    getSunriseAndSunsetInLocationOnParticularDay(
                        location.latitude, location.longitude, currentHour, location.timezoneOffset
                    )

                if (sunriseAndSunset != null && hourlyForecastEntity != null) {
                    emit(
                        hourlyForecastEntity.toCurrentForecastWithLocation(
                            sunriseAndSunset.first,
                            sunriseAndSunset.second,
                            location.toLocationModel()
                        )
                    )
                }
            }
        }
    }

    suspend fun getHourlyForecastForSpecificLocation(
        latitude: Double,
        longitude: Double,
        numberOfForecasts: Int = 24
    ): Flow<List<HourlyForecastWithLocation>> {
        if (numberOfForecasts <= 0) {
            throw IllegalArgumentException("Number of forecasts must be positive!")
        }

        return flow {
            val currentHour = currentHourFromEpochInSeconds()
            val location = dao.getSpecificLocation(latitude, longitude)?.toLocationModel()

            if (location != null) {
                emit(dao.getHourlyForecastForSpecificLocation(
                    latitude, longitude, currentHour, numberOfForecasts
                ).map { it.toHourlyForecastWithLocationModel(location) })
            }
        }
    }

    private fun currentHourFromEpochInSeconds() =
        Instant.now().truncatedTo(ChronoUnit.HOURS).epochSecond

    private fun nextHourFromEpochInSeconds(minutesFromEpoch: Long) =
        Instant.ofEpochSecond(minutesFromEpoch)
            .truncatedTo(ChronoUnit.HOURS)
            .plus(1, ChronoUnit.HOURS).epochSecond

    private suspend fun getDailyForecast(
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

    suspend fun getSunriseAndSunsetInLocationOnParticularDay(
        latitude: Double,
        longitude: Double,
        minutesFromEpoch: Long,
        rawOffset: Int
    ): Pair<Long, Long>? {
        val dayStartTime = Instant
            .ofEpochSecond(minutesFromEpoch)
            .atOffset(ZoneOffset.ofTotalSeconds(rawOffset))
            .truncatedTo(ChronoUnit.DAYS)

        val dayEndTime = dayStartTime.plusHours(24)

        val dailyForecast = withContext(Dispatchers.IO) {
            return@withContext getDailyForecast(
                latitude, longitude, dayStartTime.toEpochSecond(), dayEndTime.toEpochSecond()
            ).single()
        }

        return dailyForecast?.let {
            Pair(it.sunriseTime, it.sunsetTime)
        }
    }
}