package io.farafonova.weatherapp.persistence.database

import androidx.room.Database

@Database(
    entities = [LocationEntity::class, CurrentForecastEntity::class, DailyForecastEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ExtendedWeatherDatabase : WeatherDatabase() {
    abstract override fun forecastDao(): ExtendedForecastDao
}