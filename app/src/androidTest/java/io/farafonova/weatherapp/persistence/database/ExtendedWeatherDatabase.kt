package io.farafonova.weatherapp.persistence.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.RenameColumn
import androidx.room.migration.AutoMigrationSpec

@Database(
    entities = [
        LocationEntity::class,
        CurrentForecastEntity::class,
        DailyForecastEntity::class,
        HourlyForecastEntity::class
    ],
    version = 4,
    exportSchema = true,
    autoMigrations = [
        AutoMigration (from = 1, to = 2),
        AutoMigration (from = 2, to = 3),
        AutoMigration (from = 3, to = 4, spec = ExtendedWeatherDatabase.MyAutoMigrationFrom3To4::class)
    ]
)
abstract class ExtendedWeatherDatabase : WeatherDatabase() {
    abstract override fun forecastDao(): ExtendedForecastDao


    @DeleteColumn(tableName = "current_forecast", columnName = "description")
    @DeleteColumn(tableName = "daily_forecast", columnName = "description")
    @RenameColumn(
        tableName = "hourly_forecast",
        fromColumnName = "precipitationProbability",
        toColumnName = "pop"
    )
    class MyAutoMigrationFrom3To4 : AutoMigrationSpec
}