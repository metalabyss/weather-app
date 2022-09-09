package io.farafonova.weatherapp.persistence.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import java.time.temporal.ChronoUnit

@Dao
interface ForecastDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLocations(vararg locations: LocationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentForecasts(vararg forecasts: CurrentForecastEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateLocations(vararg locations: LocationEntity)

    @Query("SELECT * FROM current_forecast WHERE lat = :latitude AND lon = :longitude")
    suspend fun getCurrentForecastForSpecificLocation(
        latitude: Double,
        longitude: Double
    ): CurrentForecastEntity?

    @Query("SELECT * FROM location WHERE in_favorites = 1")
    suspend fun getAllFavoriteLocations(): List<LocationEntity>

    @Query(
        "SELECT * FROM current_forecast " +
                "JOIN location ON location.lat = current_forecast.lat " +
                "AND location.lon = current_forecast.lon " +
                "WHERE location.in_favorites = 1"
    )
    fun getCurrentForecastForAllFavoriteLocations(): Flow<Map<LocationEntity, CurrentForecastEntity>>

    @Query("SELECT COUNT(*) > 0 FROM location WHERE lat = :latitude AND lon = :longitude")
    suspend fun isThereAlreadySuchLocation(latitude: Double, longitude: Double): Boolean

    @Query("SELECT * FROM location WHERE lat = :latitude AND lon = :longitude")
    suspend fun getSpecificLocation(latitude: Double, longitude: Double): LocationEntity?

    @Query(
        "SELECT EXISTS(SELECT * FROM location WHERE lat = :latitude AND lon = :longitude" +
                " AND location.in_favorites = 1)"
    )
    suspend fun isLocationAlreadyInFavorites(latitude: Double, longitude: Double): Boolean

    @Query(
        "SELECT * FROM hourly_forecast WHERE lat = :latitude AND lon = :longitude"
                + " AND forecast_time >= :timeLimit LIMIT :numberOfRows"
    )
    suspend fun getHourlyForecastForSpecificLocation(
        latitude: Double,
        longitude: Double,
        timeLimit: Long,
        numberOfRows: Int = 24
    ): List<HourlyForecastEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHourlyForecasts(vararg forecasts: HourlyForecastEntity)

    @Query("DELETE FROM hourly_forecast WHERE forecast_time < :timeLimit")
    suspend fun deleteOutdatedHourlyForecasts(timeLimit: Long = Instant.now().epochSecond)

    @Query("SELECT * FROM daily_forecast WHERE lat = :latitude" +
            " AND lon = :longitude" +
            " AND forecast_time >= :lowerTimeBound" +
            " AND forecast_time <= :upperTimeBound"
    )
    suspend fun getDailyForecasts(
        latitude: Double,
        longitude: Double,
        lowerTimeBound: Long = Instant.now().epochSecond,
        upperTimeBound: Long = Instant.now().plus(7, ChronoUnit.DAYS).epochSecond
    ): List<DailyForecastEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyForecasts(vararg forecasts: DailyForecastEntity)

    @Transaction
    suspend fun insertOverallForecast(
        currentForecast: CurrentForecastEntity,
        hourlyForecasts: List<HourlyForecastEntity>,
        dailyForecasts: List<DailyForecastEntity>
    ) {
        insertCurrentForecasts(currentForecast)
        insertHourlyForecasts(*hourlyForecasts.toTypedArray())
        insertDailyForecasts(*dailyForecasts.toTypedArray())
    }
}