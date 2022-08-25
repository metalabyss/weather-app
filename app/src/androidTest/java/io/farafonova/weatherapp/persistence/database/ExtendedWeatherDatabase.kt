package io.farafonova.weatherapp.persistence.database

import androidx.room.AutoMigration
import androidx.room.Database

@Database(
    entities = [
        LocationEntity::class,
        CurrentForecastEntity::class,
        DailyForecastEntity::class,
        HourlyForecastEntity::class
    ],
    version = 3,
    exportSchema = true,
    autoMigrations = [
        AutoMigration (from = 1, to = 2),
        AutoMigration (from = 2, to = 3)
    ]
)
abstract class ExtendedWeatherDatabase : WeatherDatabase() {
    abstract override fun forecastDao(): ExtendedForecastDao
}