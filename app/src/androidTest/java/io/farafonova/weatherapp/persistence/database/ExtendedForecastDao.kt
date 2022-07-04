package io.farafonova.weatherapp.persistence.database

import androidx.room.Dao
import androidx.room.Query

@Dao
interface ExtendedForecastDao : ForecastDao {
    @Query("SELECT * FROM location")
    suspend fun getAllLocations(): List<LocationEntity>
}