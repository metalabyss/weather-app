package io.farafonova.weatherapp.persistence.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ForecastDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLocations(vararg locations: LocationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentForecast(vararg forecasts: CurrentForecastEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateLocations(vararg locations: LocationEntity)

    @Query("SELECT * FROM current_forecast WHERE lat = :latitude AND lon = :longitude")
    suspend fun getCurrentForecastForSpecificLocation(
        latitude: Float,
        longitude: Float
    ): CurrentForecastEntity

    @Query("SELECT * FROM location WHERE in_favorites = 'TRUE'")
    suspend fun getAllFavoriteLocations(): List<LocationEntity>

    @Query(
        "SELECT * FROM current_forecast " +
                "JOIN location ON location.lat = current_forecast.lat " +
                "AND location.lon = current_forecast.lon " +
                "WHERE location.in_favorites = 'TRUE'"
    )
    fun getCurrentForecastForAllFavoriteLocations(): Flow<Map<LocationEntity, CurrentForecastEntity>>
}