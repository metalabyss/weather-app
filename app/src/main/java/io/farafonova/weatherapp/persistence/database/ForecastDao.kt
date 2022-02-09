package io.farafonova.weatherapp.persistence.database

import androidx.room.*

@Dao
interface ForecastDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLocations(vararg locations: LocationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentForecast(vararg forecasts: CurrentForecastEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateLocations(vararg locations: LocationEntity)

    @Query("SELECT * FROM current_forecast WHERE lat = :latitude AND lon = :longitude")
    suspend fun getCurrentForecastForSpecificLocation(latitude: Float, longitude: Float): CurrentForecastEntity
}